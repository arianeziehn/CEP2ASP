import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
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
 * Run with these parameters
 * --input ./src/main/resources/QnV.csv
 */

public class Q6_ITERQuery_I1_6_IVJ {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        long throughput = parameters.getLong("tput", 0);
        int times = parameters.getInt("times", 5);
        Integer velFilter = parameters.getInt("vel", 179);
        Integer windowSize = parameters.getInt("wsize", 15);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ6_I1T5_ASP_IVJ.csv");
        } else {
            outputPath = parameters.get("output");
        }
        // Local settings may require you to adjust configs in this way, we experienced it for longer iterations
        //Configuration cfg = new Configuration();
        //int defaultLocalParallelism = Runtime.getRuntime().availableProcessors();
        //cfg.setString("taskmanager.memory.network.fraction", "0.2");
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(defaultLocalParallelism, cfg);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input
                .filter(t -> ((Double) t.getValue()) >= velFilter && (t instanceof VelocityEvent))
                .map(new UDFs.MapKey());

        // iter2
        // iter2
        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it2 = velStream.keyBy(new UDFs.getArtificalKey())
                .intervalJoin(velStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs() && (Double) d1.f0.getValue() < (Double) d2.f0.getValue()) {
                            collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

        DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it3 = it2.keyBy(new UDFs.getArtificalKeyT4())
                .intervalJoin(velStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs() && (Double) d1.f1.getValue() < (Double) d2.f0.getValue()) {
                            collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt(60000));

        DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it4 = it3
                .keyBy(new UDFs.getArtificalKeyT5())
                .intervalJoin(velStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs() && (Double) d1.f2.getValue() < (Double) d2.f0.getValue()) {
                            collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d2.f0, d1.f3, 1));
                         }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp4KeyedDataPointGeneralLongInt(60000));

        //iter 5
        DataStream<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it5 = it4
                .keyBy(new UDFs.getArtificalKeyT6())
                .intervalJoin(velStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f3.getTimeStampMs() < d2.f0.getTimeStampMs() && (Double) d1.f3.getValue() < (Double) d2.f0.getValue()) {
                            collector.collect(new Tuple7<>(d1.f0, d1.f1, d1.f2, d1.f3, d2.f0, d1.f4, 1));
                            }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp5KeyedDataPointGeneralLongInt(60000));

        DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it6 = it5
                .keyBy(new UDFs.getArtificalKeyT7())
                .intervalJoin(velStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void processElement(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f4.getTimeStampMs() < d2.f0.getTimeStampMs() && (Double) d1.f4.getValue() < (Double) d2.f0.getValue()) {
                            collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d2.f0));
                        }
                    }
                });

        it6.flatMap(new LatencyLoggerT6(true));//.print();
        it6.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}

