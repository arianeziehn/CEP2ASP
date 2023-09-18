import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
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

/**
 * Run with these parameters
 * --input ./src/main/resources/QnV_R2000070.csv
 */

public class Q11_ITERQuery_I1_4 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 114);
        Integer sensors = parameters.getInt("sensors", 16);
        Integer windowSize = parameters.getInt("wsize", 90);
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ11_I1T3_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> velStream = input
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent));

        // iter2
        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it2 = velStream.join(velStream)
                .where(KeyedDataPointGeneral::getKey)
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    // we use a HashSet to maintain duplicates
                    final HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> set = new HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>(1000);
                    @Override
                    public void join(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.getTimeStampMs() < d2.getTimeStampMs()) {
                            Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> result = new Tuple3<>(d1, d2, d1.getTimeStampMs());
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
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLong(60000));

        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it3 = it2.join(velStream)
                .where(new UDFs.getKeyT3())
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    final HashSet<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> set = new HashSet<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>(1000);
                    @Override
                    public void join(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.getTimeStampMs()) {
                            Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> result = new Tuple4<>(d1.f0, d1.f1, d2, d1.f2);
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
                })
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLong(60000));

        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it4 = it3.join(velStream)
                .where(new UDFs.getKeyT4())
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    final HashSet<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>();
                    @Override
                    public void join(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f2.getTimeStampMs() < d2.getTimeStampMs()) {
                            Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple4<>(d1.f0, d1.f1, d1.f2, d2);
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

        //it4.flatMap(new LatencyLoggerT4());
        it4//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}

