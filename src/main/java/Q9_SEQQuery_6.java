import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.*;
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
 * Run with these parameters:
 *  --inputQnV ./src/main/resources/QnV_R2000070.csv --inputPM ./src/main/resources/luftdaten_11245.csv --inputTH ./src/main/resources/luftdaten_11246.csv
 */

public class Q9_SEQQuery_6 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("inputQnV")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("inputQnV");
        String file1 = parameters.get("inputPM");
        String file2 = parameters.get("inputTH");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        String outputPath;
        int patternLength = parameters.getInt("pattern", 5);
        long throughput = parameters.getLong("tput", 100000);
        Integer velFilter = parameters.getInt("vel", 103);
        Integer quaFilter = parameters.getInt("qua", 101);
        Integer windowSize = parameters.getInt("wsize", 15);
        Integer pm2Filter = parameters.getInt("pms", 25);
        Integer pm10Filter = parameters.getInt("pmb", 27);
        Integer tempFilter = parameters.getInt("temp", 17);
        Integer humFilter = parameters.getInt("hum", 45);
        long tputQnV = (long) (throughput * 0.6);
        long tputPM = (long) (throughput * 0.19);
        long tputLD = (long) (throughput * 0.21);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ7_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input1 = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", tputQnV))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> input2 = env.addSource(new KeyedDataPointSourceFunction(file1, iterations, ";", tputPM))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        DataStream<KeyedDataPointGeneral> input3 = env.addSource(new KeyedDataPointSourceFunction(file2, iterations, ";", tputLD))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        input1.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputQnV));
        input2.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPM));
        input3.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputLD));


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input1
                .filter(t -> {
                    return ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent);
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = input1
                .filter(t -> {
                    return ((Double) t.getValue()) > quaFilter && t instanceof QuantityEvent;
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm2Stream = input2
                .filter(t -> {
                    return ((Double) t.getValue()) > pm2Filter && (t instanceof PartMatter2Event);
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm10Stream = input2
                .filter(t -> {
                    return ((Double) t.getValue()) > pm10Filter && t instanceof PartMatter10Event;
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> temperature = input3
                .filter(t -> {
                    return ((Double) t.getValue()) > tempFilter && (t instanceof TemperatureEvent);
                })
                .map(new UDFs.MapKey());


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> humidity = input3
                .filter(t -> {
                    return ((Double) t.getValue()) > humFilter && t instanceof HumidityEvent;
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> seq2 = velStream.join(quaStream)
                .where(new UDFs.getArtificalKey())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    final HashSet<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> set = new HashSet<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>(1000);
                    @Override
                    public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> result = new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1);
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
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

        DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> seq3 = seq2.join(pm2Stream)
                .where(new UDFs.getArtificalKeyT4())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    final HashSet<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> set = new HashSet<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>();
                    @Override
                    public void join(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, Long, Integer> result = new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1);
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
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt());

        DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> seq4 = seq3.join(pm10Stream)
                .where(new UDFs.getArtificalKeyT5())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    final HashSet<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> set = new HashSet<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>();
                    @Override
                    public void join(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> result = new Tuple6<>(d1.f0, d1.f1, d1.f2, d2.f0, d1.f3, 1);
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
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp4KeyedDataPointGeneralLongInt());

        if (patternLength == 5) {
             DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> seq5 = seq4.join(temperature)
                    .where(new UDFs.getArtificalKeyT6())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                        final HashSet<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>();

                        @Override
                        public void join(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                            if (d1.f3.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple5<>(d1.f0, d1.f1, d1.f2, d1.f3, d2.f0);
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

            seq5.flatMap(new LatencyLoggerT5(true));
            seq5.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        } else if (patternLength == 6) {

            DataStream<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> seq5 = seq4.join(temperature)
                    .where(new UDFs.getArtificalKeyT6())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        final HashSet<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> set = new HashSet<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>();
                        @Override
                        public void join(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f3.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> result = new Tuple7<>(d1.f0, d1.f1, d1.f2, d1.f3, d2.f0, d1.f4, 1);
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
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp5KeyedDataPointGeneralLongInt());

            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> seq6 = seq5.join(humidity)
                    .where(new UDFs.getArtificalKeyT7())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                        final HashSet<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>();

                        @Override
                        public void join(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                            if (d1.f4.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple6<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d2.f0);
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

            seq6.flatMap(new LatencyLoggerT6(true));
            seq6.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
