import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.shaded.guava18.com.google.common.collect.Ordering;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SEQ( Q1, -V1, PM2)
 * Run with these parameters: they have matching event time
 * --input ./src/main/resources/QnV_R2000070.csv --inputAQ ./src/main/resources/luftdaten_11245.csv
 */

public class QNSEQ_E1 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        String outputPath;
        Integer quaFilter = parameters.getInt("qua", 80);
        Integer velFilter = parameters.getInt("vel", 175);
        Integer windowSize = parameters.getInt("wsize", 100);
        Integer selectivity = parameters.getInt("sel", 1); // int between (0 and 100]
        Integer runtimeMinutes = parameters.getInt("run", 1); // time the source generates events in min
        Integer pm10Filter = parameters.getInt("pm10", 30);
        long throughput = parameters.getLong("tput", 100000);
        long tputPerStream = (long) (throughput * 0.34);

        if (!parameters.has("output")) {
            outputPath = "./src/main/resources/Result_QNSEQ_E1.csv";
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputQuantity = env.addSource(new ArtificalSourceFunction("Quantity", tputPerStream, windowSize, runtimeMinutes, 0.2, 0.21, selectivity))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputPM10 = env.addSource(new ArtificalSourceFunction("PM10", tputPerStream, windowSize, runtimeMinutes, 0.6, 0.61, selectivity))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputVelocity = env.addSource(new ArtificalSourceFunction("Velocity", tputPerStream, windowSize, runtimeMinutes, 0.2, 0.61, selectivity, "NEG"))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        inputQuantity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));
        inputPM10.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));
        inputVelocity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));

        DataStream<KeyedDataPointGeneral> quaStream = inputQuantity.filter(t -> ((Double) t.getValue() > quaFilter));
        DataStream<KeyedDataPointGeneral> velStream = inputVelocity.filter(t -> ((Double) t.getValue() < velFilter));
        DataStream<KeyedDataPointGeneral> PM10Stream = inputPM10.filter(t -> ((Double) t.getValue() > pm10Filter));


        DataStream<Tuple2<KeyedDataPointGeneral, Long>> quaStreamWithNextVelocityEvent = quaStream.union(velStream)
                .keyBy(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                // we create a WindowUDF to order the window content and determine the next occurrence of a velocity event
                .apply(new WindowFunction<KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral, Long>, String, TimeWindow>() {
                    //final HashSet<KeyedDataPointGeneral> set = new HashSet<KeyedDataPointGeneral>(1000);
                    @Override
                    public void apply(String key, TimeWindow timeWindow, Iterable<KeyedDataPointGeneral> iterable, Collector<Tuple2<KeyedDataPointGeneral, Long>> collector) throws Exception {
                        List<KeyedDataPointGeneral> list = new ArrayList<KeyedDataPointGeneral>();
                        for (KeyedDataPointGeneral data : iterable) {
                            list.add(data);
                        }
                        // sort events by time
                        list = Ordering.from(new UDFs.TimeComparatorKDG()).sortedCopy(list);
                        //find for each quantity event the next velocity event
                        for (int i = 0; i < list.size(); i++) {
                            KeyedDataPointGeneral data = list.get(i);
                            boolean followedBy = false;
                            //System.out.println(timeWindow.getEnd());
                            //System.out.println(data.getTimeStampMs());
                            //System.out.println(timeWindow.getEnd() - data.getTimeStampMs() >= Time.minutes(100).toMilliseconds());
                            if (data instanceof QuantityEvent && (timeWindow.getEnd() - data.getTimeStampMs() >= Time.minutes(100).toMilliseconds())) {
                                // we only need to check if the tuple is a relevant QuantityEvent
                                for (int j = i + 1; j < list.size(); j++) { // then we check all successors
                                    KeyedDataPointGeneral follow = list.get(j);
                                    if (follow instanceof VelocityEvent && follow.getTimeStampMs() > data.getTimeStampMs() & (follow.getTimeStampMs() - data.getTimeStampMs() <= Time.minutes(100).toMilliseconds())) {
                                        // only successors that are velocity events are of interest
                                        collector.collect(new Tuple2<KeyedDataPointGeneral, Long>(data, follow.getTimeStampMs()));
                                        // for each quantity event only the next following velocity event is relevant
                                        followedBy = true;
                                        // set.add(data);
                                        break;
                                    }
                                }
                                // also valid if no velocity event occurs at all (i.e., no predicate on the event type)
                                if (!followedBy) {
                                    long ts = data.getTimeStampMs() + Time.minutes(100).toMilliseconds();
                                    collector.collect(new Tuple2<KeyedDataPointGeneral, Long>(data, ts));
                                }
                            }
                        }
                        //we need to assign the event timestamp to the new stream again to guarantee the time constraints of the sequence operator
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestampNOT2(60000));

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = quaStreamWithNextVelocityEvent.join(PM10Stream)
                .where(new KeySelector<Tuple2<KeyedDataPointGeneral, Long>, String>() {
                    @Override
                    public String getKey(Tuple2<KeyedDataPointGeneral, Long> data) throws Exception {
                        return data.f0.getKey();
                    }
                })
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    final HashSet<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>(1000);

                    @Override
                    public void join(Tuple2<KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        // we check if in between the events is no velocity event
                        if ((d1.f1 >= d2.getTimeStampMs() && d1.f0.getTimeStampMs() < d2.getTimeStampMs())) {
                            Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple2<>(d1.f0, d2);
                            if (!set.contains(result)) {
                                if (set.size() == 1000) {
                                    set.removeAll(set);
                                    // to maintain the HashSet Size we flush after 1000 entries
                                }
                                collector.collect(result);
                                set.add(result);
                            }
                        }
                    }
                });

        result.flatMap(new LatencyLoggerT2());
        result//.print();
              .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
