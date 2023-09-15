import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
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
 * --inputQnV ./src/main/resources/QnV_R2000070.csv --inputPM ./src/main/resources/luftdaten_11245.csv
 */

public class Q9_SEQQuery_IVJ_4 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("inputQnV")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("inputQnV");
        String file1 = parameters.get("inputPM");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 104);
        Integer quaFilter = parameters.getInt("qua", 104);
        Integer windowSize = parameters.getInt("wsize", 15);
        int patternLength = parameters.getInt("pattern", 3);
        Integer pm2Filter = parameters.getInt("pms", 15);
        Integer pm10Filter = parameters.getInt("pmb", 5);
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ9_ASP4_IVJ.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input1 = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> input2 = env.addSource(new KeyedDataPointSourceFunction(file1, iterations, ";", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        input1.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));
        input2.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input1
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = input1
                .filter(t -> ((Double) t.getValue()) > quaFilter && t instanceof QuantityEvent)
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm2Stream = input2
                .filter(t -> ((Double) t.getValue()) > pm2Filter && (t instanceof PartMatter2Event))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm10Stream = input2
                .filter(t -> ((Double) t.getValue()) > pm10Filter && t instanceof PartMatter10Event)
                .map(new UDFs.MapKey());

        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> seq2 = velStream
                .keyBy(new UDFs.getArtificalKey())
                .intervalJoin(quaStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {

                    @Override
                    public void processElement(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                        }
                    }
                })
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

        if (patternLength == 3) {

            DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> seq3 = seq2
                    .keyBy(new UDFs.getArtificalKeyT4())
                    .intervalJoin(pm2Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.minutes(0), Time.minutes(windowSize))
                    .lowerBoundExclusive()
                    .upperBoundExclusive()
                    .process(new ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                        @Override
                        public void processElement(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple3<>(d1.f0,d1.f1, d2.f0));
                            }
                        }
                    });

            seq3.flatMap(new LatencyLoggerT3(true));
            seq3//.print();
                    .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        } else if (patternLength == 4) {

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> seq3 = seq2
                    .keyBy(new UDFs.getArtificalKeyT4())
                    .intervalJoin(pm2Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.minutes(0), Time.minutes(windowSize))
                    .lowerBoundExclusive()
                    .upperBoundExclusive()
                    .process(new ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void processElement(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt(60000));

            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> seq4 = seq3.keyBy(new UDFs.getArtificalKeyT5())
                    .intervalJoin(pm10Stream.keyBy(new UDFs.getArtificalKey()))
                    .between(Time.minutes(0), Time.minutes(windowSize))
                    .lowerBoundExclusive()
                    .upperBoundExclusive()
                    .process(new ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                        @Override
                        public void processElement(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                            if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple4<>(d1.f0, d1.f1, d1.f2, d2.f0));
                            }
                        }
                    });

            seq4.flatMap(new LatencyLoggerT4(true));
            seq4//.print();
                    .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
