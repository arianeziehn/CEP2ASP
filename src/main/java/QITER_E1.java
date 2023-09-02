import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import util.*;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Run with these parameters
 * --input ./src/main/resources/QnV.csv
 */

public class QITER_E1 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 175);
        Integer windowSize = parameters.getInt("wsize", 100);
        Integer selectivity = parameters.getInt("sel", 1); // int between (0 and 100]
        Integer runtimeMinutes = parameters.getInt("run", 1); // time the source generates events in min
        Integer times = parameters.getInt("times", 3);
        selectivity = selectivity * times;
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = "./src/main/resources/Result_QITER_E1.csv";
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputVelocity = env.addSource(new ArtificalSourceFunction("Velocity", throughput, windowSize, runtimeMinutes, selectivity, "ITER"))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        inputVelocity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> velStream = inputVelocity.filter(t -> ((Double) t.getValue()) > velFilter);

        // iter2
        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it2 = velStream.join(velStream)
                .where(KeyedDataPointGeneral::getKey)
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    // we use a HashSet to maintain duplicates
                    final HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> set = new HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>(100);

                    @Override
                    public void join(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.getValue() < (Double) d2.getValue()) {
                            Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> result = new Tuple3<>(d1, d2, d1.getTimeStampMs());
                            if (!set.contains(result)) {
                                collector.collect(result);
                                if (set.size() == 100) {
                                    set.removeAll(set);
                                    // to maintain the HashSet Size we flush after 1000 entries
                                }
                                set.add(result);
                            }

                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLong(60000));

        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it3 = it2.join(velStream)
                .where(new UDFs.getKeyT3())
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    // we use a HashSet to maintain duplicates
                    final HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>(100);

                    @Override
                    public void join(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f1.getValue() < (Double) d2.getValue()) {
                            Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple3<>(d1.f0, d1.f1, d2);
                            if (!set.contains(result)) {
                                if (set.size() == 100) {
                                    set.removeAll(set);
                                    // to maintain the HashSet Size we flush after 1000 entries
                                }
                                collector.collect(result);
                                set.add(result);

                            }
                        }
                    }
                });

        it3.flatMap(new LatencyLogger());
        it3//.print();
          .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}

