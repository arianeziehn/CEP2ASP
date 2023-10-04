import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
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

public class Q9_SEQQueryLength6_IntervalJoin {
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
        int patternLength = parameters.getInt("pattern", 6);
        long throughput = parameters.getLong("tput", 100000);
        Integer velFilter = parameters.getInt("vel", 115);
        Integer quaFilter = parameters.getInt("qua", 105);
        Integer windowSize = parameters.getInt("wsize", 15);
        Integer pm2Filter = parameters.getInt("pm2", 5);
        Integer pm10Filter = parameters.getInt("pm10", 5);
        Integer tempFilter = parameters.getInt("temp", 15);
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

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> stream1 = input1
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> stream2 = input2
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> stream3 = input3
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000))
                .map(new UDFs.MapKey());


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = stream1
                .filter(t -> ((Double) t.f0.getValue()) > velFilter && (t.f0 instanceof VelocityEvent));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = stream1
                .filter(t -> ((Double) t.f0.getValue()) > quaFilter && t.f0 instanceof QuantityEvent);

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm2Stream = stream2
                .filter(t -> ((Double) t.f0.getValue()) > pm2Filter && (t.f0 instanceof PartMatter2Event));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm10Stream = stream2
                .filter(t -> ((Double) t.f0.getValue()) > pm10Filter && t.f0 instanceof PartMatter10Event);

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> temperature = stream3
                .filter(t -> {
                    return ((Double) t.f0.getValue()) > tempFilter && (t.f0 instanceof TemperatureEvent);
                });


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> humidity = stream3
                .filter(t -> {
                    return ((Double) t.f0.getValue()) > humFilter && t.f0 instanceof HumidityEvent;
                });


        if (patternLength == 5) {
            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it1 = velStream.keyBy(new UDFs.getArtificalKey())
                    .intervalJoin(quaStream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {

                        @Override
                        public void processElement(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                            }
                        }
                    })
                    .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it2 = it1.keyBy(new UDFs.getArtificalKeyT4())
                    .intervalJoin(pm2Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void processElement(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt());

            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it3 = it2.keyBy(new UDFs.getArtificalKeyT5())
                    .intervalJoin(pm10Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void processElement(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d2.f0, d1.f3, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp4KeyedDataPointGeneralLongInt());

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = it3.keyBy(new UDFs.getArtificalKeyT6())
                    .intervalJoin(temperature.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                        @Override
                        public void processElement(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                            if (d1.f3.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d1.f2, d1.f3, d2.f0));
                            }
                        }
                    });

            result.flatMap(new LatencyLoggerT5(true));
            result //.print();
                    .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        } else if (patternLength == 6) {
            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it1 = velStream.keyBy(new UDFs.getArtificalKey())
                    .intervalJoin(quaStream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {

                        @Override
                        public void processElement(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                            }
                        }
                    })
                    .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it2 = it1.keyBy(new UDFs.getArtificalKeyT4())
                    .intervalJoin(pm2Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void processElement(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt());

            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it3 = it2.keyBy(new UDFs.getArtificalKeyT5())
                    .intervalJoin(pm10Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void processElement(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d2.f0, d1.f3, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp4KeyedDataPointGeneralLongInt());

            DataStream<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it4 = it3.keyBy(new UDFs.getArtificalKeyT6())
                    .intervalJoin(temperature.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void processElement(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f3.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple7<>(d1.f0, d1.f1, d1.f2, d1.f3, d2.f0, d1.f4, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp5KeyedDataPointGeneralLongInt());

            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = it4.keyBy(new UDFs.getArtificalKeyT7())
                    .intervalJoin(humidity.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                    .process(new ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                        @Override
                        public void processElement(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                            if (d1.f4.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d2.f0));
                            }
                        }
                    });

            result.flatMap(new LatencyLoggerT6(true));
            result //.print();
                    .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
