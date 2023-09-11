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
 * --input ./src/main/resources/QnV_large.csv
 */

public class Q6_ITERQueryI1T9_IntervalJoin {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        long throughput = parameters.getLong("tput", 0);
        int times = parameters.getInt("times", 9);
        Integer velFilter = parameters.getInt("vel", 168);
        Integer windowSize = parameters.getInt("wsize", 15);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ6_I1T9_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        // Local settings may require you to adjust configs in this way, we experienced it for longer iterations
        Configuration cfg = new Configuration();
        int defaultLocalParallelism = Runtime.getRuntime().availableProcessors();
        cfg.setString("taskmanager.memory.network.fraction", "0.2");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(defaultLocalParallelism, cfg);
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> velStream = input
                .filter(t -> ((Double) t.getValue()) >= velFilter && (t instanceof VelocityEvent));

        // iter2
        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it2 = velStream.keyBy(KeyedDataPointGeneral::getKey)
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    @Override
                    public void processElement(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>.Context context, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple3<>(d1, d2, d1.getTimeStampMs()));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLong(60000));

        DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer>> it3 = it2.keyBy(new UDFs.getKeyT3())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer>>() {
                    @Override
                    public void processElement(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer>>.Context context, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f1.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple5<>(d1.f0,d1.f1, d2, d1.f1.getTimeStampMs(),1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt(60000));


        DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it4 = it3
                .keyBy(new UDFs.getKeyT4())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer>, KeyedDataPointGeneral, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long,Integer>, KeyedDataPointGeneral, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f2.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f2.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple6<>(d1.f0,d1.f1,d1.f2, d2, d1.f2.getTimeStampMs(),1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp4KeyedDataPointGeneralLongInt(60000));

        //iter 5
        DataStream<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it5 = it4
                .keyBy(new UDFs.getKeyT5())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f3.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f3.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple7<>(d1.f0,d1.f1,d1.f2,d1.f3, d2, d1.f3.getTimeStampMs(),1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp5KeyedDataPointGeneralLongInt(60000));

        DataStream<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it6 = it5
                .keyBy(new UDFs.getKeyT6())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f4.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f4.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple8<>(d1.f0,d1.f1,d1.f2,d1.f3,d1.f4, d2, d1.f4.getTimeStampMs(),1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp6KeyedDataPointGeneralLongInt(60000));

        //iter 7
        DataStream<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it7 = it6
                .keyBy(new UDFs.getKeyT7())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f5.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f5.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple9<>(d1.f0,d1.f1,d1.f2,d1.f3,d1.f4,d1.f5, d2, d1.f5.getTimeStampMs(),1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp7KeyedDataPointGeneralLongInt(60000));

        DataStream<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it8 = it7
                .keyBy(new UDFs.getKeyT8())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f6.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f6.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple10<>(d1.f0,d1.f1,d1.f2,d1.f3,d1.f4,d1.f5,d1.f6, d2, d1.f6.getTimeStampMs(),1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp8KeyedDataPointGeneralLongInt(60000));

        //iter 9
        DataStream<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it9 = it8
                .keyBy(new UDFs.getKeyT9())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void processElement(Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, KeyedDataPointGeneral, Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f7.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f7.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple9<>(d1.f0,d1.f1,d1.f2,d1.f3,d1.f4,d1.f5,d1.f6,d1.f7, d2));
                        }
                    }
                });

        it9.flatMap(new LatencyLoggerT9());
        it9//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}