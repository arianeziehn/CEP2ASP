import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SEQ( Q1, -V1, PM2)
 * Run with these parameters: they have matching event time
 * --input ./src/main/resources/QnV_R2000070.csv --inputAQ ./src/main/resources/luftdaten_11245.csv
 */

public class Q5_NOTQuery {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input") || !parameters.has("inputAQ")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String file1 = parameters.get("inputAQ");
        Integer velFilter = parameters.getInt("vel", 100);
        Integer quaFilter = parameters.getInt("qua", 75);
        Integer pm2Filter = parameters.getInt("qua", 40);
        Integer windowSize = parameters.getInt("wsize", 15);
        String outputPath;
        long throughput = parameters.getLong("tput", 0);
        long tputQnV = 0;
        long tputPM = 0;
        if (throughput > 0) {
            tputQnV = (long) (throughput * 0.67);
            tputPM = (long) (throughput * 0.33);
        }
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ5_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputQnV = env.addSource(new KeyedDataPointSourceFunction(file,  ",", tputQnV))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputAQ = env.addSource(new KeyedDataPointSourceFunction(file1, ";", tputPM))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        inputQnV.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputQnV));
        inputAQ.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPM));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = inputQnV.filter(t -> ((Double) t.getValue() > quaFilter && t instanceof QuantityEvent) || (((Double) t.getValue() < velFilter) && t instanceof VelocityEvent))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> PM2Stream = inputAQ.filter(t -> ((Double) t.getValue()) > pm2Filter && t instanceof PartMatter2Event)
                .map(new UDFs.MapKey());

        DataStream<Tuple3<KeyedDataPointGeneral, Long, Integer>> quaStreamWithNextVelocityEvent = quaStream
                .keyBy(1)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                // we create a WindowUDF to order the window content and determine the next occurance of a velocity event
                .apply(new WindowFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple3<KeyedDataPointGeneral, Long, Integer>, Tuple, TimeWindow>() {
                    @Override
                    public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<Tuple2<KeyedDataPointGeneral, Integer>> iterable, Collector<Tuple3<KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        List<Tuple2<KeyedDataPointGeneral, Integer>> list = new ArrayList<Tuple2<KeyedDataPointGeneral, Integer>>();
                        for (Tuple2<KeyedDataPointGeneral, Integer> data : iterable) {
                            list.add(data);
                        }
                        // order events by time
                        list = Ordering.from(new Comparator<Tuple2<KeyedDataPointGeneral, Integer>>() {
                            @Override
                            public int compare(Tuple2<KeyedDataPointGeneral, Integer> t1, Tuple2<KeyedDataPointGeneral, Integer> t2) {
                                if (t1.f0.getTimeStampMs() < t2.f0.getTimeStampMs()) return -1;
                                if (t1.f0.getTimeStampMs() == t2.f0.getTimeStampMs()) return 0;
                                if (t1.f0.getTimeStampMs() > t2.f0.getTimeStampMs()) return 1;
                                return 0;
                            }
                        }).sortedCopy(list);
                        //find for each quantity event the next velocity event
                        for (int i = 0; i < list.size(); i++) {
                            Tuple2<KeyedDataPointGeneral, Integer> data = list.get(i);
                            boolean followedBy = false;
                            if (data.f0 instanceof QuantityEvent && (timeWindow.getEnd() - data.f0.getTimeStampMs() >= Time.minutes(15).toMilliseconds())) {
                                for (int j = i + 1; j < list.size(); j++) {
                                    Tuple2<KeyedDataPointGeneral, Integer> follow = list.get(j);
                                    if (follow.f0 instanceof VelocityEvent && follow.f0.getTimeStampMs() > data.f0.getTimeStampMs() & (follow.f0.getTimeStampMs() - data.f0.getTimeStampMs() <= Time.minutes(16).toMilliseconds())) {
                                        collector.collect(new Tuple3<KeyedDataPointGeneral, Long, Integer>(data.f0, follow.f0.getTimeStampMs(), 1));
                                        followedBy = true;
                                        break;
                                    }
                                }
                                // also valid if no velocity event follows
                                if (!followedBy) {
                                    long ts = data.f0.getTimeStampMs() + Time.minutes(windowSize).toMilliseconds();
                                    collector.collect(new Tuple3<KeyedDataPointGeneral, Long, Integer>(data.f0, ts, 1));
                                }
                            }
                        }
                        //we need to assign the event timestamp to the new stream again to guarantee the time constraints of the sequence operator
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestampNOT(60000));

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = quaStreamWithNextVelocityEvent.join(PM2Stream)
                .where(new KeySelector<Tuple3<KeyedDataPointGeneral, Long, Integer>, Integer>() {
                    @Override
                    public Integer getKey(Tuple3<KeyedDataPointGeneral, Long, Integer> data) throws Exception {
                        return data.f2;
                    }
                })
                .equalTo(new KeySelector<Tuple2<KeyedDataPointGeneral, Integer>, Integer>() {
                    @Override
                    public Integer getKey(Tuple2<KeyedDataPointGeneral, Integer> data) throws Exception {
                        return data.f1;
                    }
                })
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple3<KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void join(Tuple3<KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        // we check if in between the events is no velocity event
                        if ((d1.f1 >= d2.f0.getTimeStampMs() && d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs())) {
                            collector.collect(new Tuple2<>(d1.f0, d2.f0));
                        }
                    }
                });

        result //.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }


}
