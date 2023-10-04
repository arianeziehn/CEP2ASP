import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import util.*;

import java.util.concurrent.TimeUnit;

public class Q9_SEQQueryLength10_IS {

    public static void main(String[] args) throws Exception {

        String className = "Q9_SEQQueryLength10_IS";

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        Integer velFilter = parameters.getInt("vel", 150);
        Integer quaFilter = parameters.getInt("qua", 250);
        Integer windowSize = parameters.getInt("wsize", 30);
        Integer pm2Filter = parameters.getInt("pm2", 250);
        Integer pm10Filter = parameters.getInt("pm10", 250);
        Integer tempFilter = parameters.getInt("temp", 250);
        Integer humFilter = parameters.getInt("hum", 250);
        Integer var7Filter = parameters.getInt("var7", 250);
        Integer var8Filter = parameters.getInt("var8", 250);
        Integer var9Filter = parameters.getInt("var9", 250);
        Integer var10Filter = parameters.getInt("var10", 250);
        long throughput = parameters.getLong("tput", 100000);
        Integer file_loops = parameters.getInt("file_loops", 1);


        String outputName = className+"/throughput_"+throughput+"_loop_"+file_loops+"/";
        String outputPath;
        if (parameters.has("output")) {
            outputPath = parameters.get("output") + outputName;
        } else {
            outputPath = "./out/" + outputName;
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input
                .filter(t -> {
                    return ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent);
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = input
                .filter(t -> {
                    return ((Double) t.getValue()) > quaFilter && t instanceof QuantityEvent;
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm2Stream = input
                .filter(t -> {
                    return ((Double) t.getValue()) > pm2Filter && (t instanceof PartMatter2Event);
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm10Stream = input
                .filter(t -> {
                    return ((Double) t.getValue()) > pm10Filter && t instanceof PartMatter10Event;
                })
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> temperature = input
                .filter(t -> {
                    return ((Double) t.getValue()) > tempFilter && (t instanceof TemperatureEvent);
                })
                .map(new UDFs.MapKey());


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> humidity = input
                .filter(t -> {
                    return ((Double) t.getValue()) > humFilter && t instanceof HumidityEvent;
                })
                .map(new UDFs.MapKey());
        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> var7 = input
                .filter(t -> {
                    return ((Double) t.getValue()) > var7Filter && t instanceof Var7Event;
                })
                .map(new UDFs.MapKey());
        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> var8 = input
                .filter(t -> {
                    return ((Double) t.getValue()) > var8Filter && t instanceof Var8Event;
                })
                .map(new UDFs.MapKey());
        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> var9 = input
                .filter(t -> {
                    return ((Double) t.getValue()) > var9Filter && t instanceof Var9Event;
                })
                .map(new UDFs.MapKey());
        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> var10 = input
                .filter(t -> {
                    return ((Double) t.getValue()) > var10Filter && t instanceof Var10Event;
                })
                .map(new UDFs.MapKey());

        int slidingWindow=20;

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

        DataStream<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it5 = it4.keyBy(new UDFs.getArtificalKeyT7())
                .intervalJoin(humidity.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f4.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple8<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d2.f0,d1.f5,1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp6KeyedDataPointGeneralLongInt());
        DataStream<Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it6 = it5.keyBy(new UDFs.getArtificalKeyT8())
                .intervalJoin(var7.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f5.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple9<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4,d1.f5, d2.f0,d1.f6,1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp7KeyedDataPointGeneralLongInt());
        DataStream<Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it7 = it6.keyBy(new UDFs.getArtificalKeyT9())
                .intervalJoin(var8.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f6.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple10<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4,d1.f5,d1.f6,d2.f0,d1.f7,1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp8KeyedDataPointGeneralLongInt());
        DataStream<Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it8 = it7.keyBy(new UDFs.getArtificalKeyT10())
                .intervalJoin(var9.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void processElement(Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>.Context context, Collector<Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f7.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple11<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4,d1.f5,d1.f6,d1.f7,d2.f0,d1.f8,1));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp9KeyedDataPointGeneralLongInt());
        DataStream<Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = it8.keyBy(new UDFs.getArtificalKeyT11())
                .intervalJoin(var10.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void processElement(Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple11<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple10<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f8.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple10<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4,d1.f5,d1.f6,d1.f7,d1.f8,d2.f0));
                        }
                    }
                });

        result.flatMap(new LatencyLoggerT10(true));
        result.writeAsText(outputPath+"result_tuples.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
        //latencies.writeAsText(outputPath+"latency.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }


}
