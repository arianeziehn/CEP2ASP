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
import java.util.concurrent.TimeUnit;

public class Q10_SEQQueryLength8 {

    public static void main(String[] args) throws Exception {

        String className = "Q10_SEQQueryLength8";

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        Integer velFilter = parameters.getInt("vel", 150);
        Integer quaFilter = parameters.getInt("qua", 250);
        Integer windowSize = parameters.getInt("wsize", 20);
        Integer pm2Filter = parameters.getInt("pm2", 250);
        Integer pm10Filter = parameters.getInt("pm10", 250);
        Integer tempFilter = parameters.getInt("temp", 250);
        Integer humFilter = parameters.getInt("hum", 250);
        Integer var7Filter = parameters.getInt("var7", 250);
        Integer var8Filter = parameters.getInt("var8", 250);
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

        DataStream<String> throughput_messages = input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

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

        int slidingWindow=20;

            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it1 = velStream.join(quaStream)
                    .where(new UDFs.getArtificalKey())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                    .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                            }
                        }
                    })
                    .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt());

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it2 = it1.join(pm2Stream)
                    .where(new UDFs.getArtificalKeyT4())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                    .apply(new FlatJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt());

            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it3 = it2.join(pm10Stream)
                    .where(new UDFs.getArtificalKeyT5())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                    .apply(new FlatJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d2.f0, d1.f3, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp4KeyedDataPointGeneralLongInt());

            DataStream<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it4 = it3.join(temperature)
                    .where(new UDFs.getArtificalKeyT6())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                    .apply(new FlatJoinFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f3.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple7<>(d1.f0, d1.f1, d1.f2, d1.f3, d2.f0, d1.f4, 1));
                            }
                        }
                    }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp5KeyedDataPointGeneralLongInt());

            DataStream<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it5 = it4.join(humidity)
                    .where(new UDFs.getArtificalKeyT7())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                    .apply(new FlatJoinFunction<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f4.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple8<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d2.f0, d1.f5, 1));
                            }
                        }
                    });

        DataStream<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, Long, Integer>> it6 = it5.join(var7)
                .where(new UDFs.getArtificalKeyT8())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                .apply(new FlatJoinFunction<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void join(Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f5.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple9<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d1.f5, d2.f0, d1.f6, 1));
                        }
                    }
                });

        DataStream<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> result = it6.join(var8)
                .where(new UDFs.getArtificalKeyT9())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(slidingWindow)))
                .apply(new FlatJoinFunction<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                    @Override
                    public void join(Tuple9<KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                        if (d1.f6.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple10<>(d1.f0, d1.f1, d1.f2, d1.f3, d1.f4, d1.f5, d1.f6, d2.f0, d1.f7, 1));
                        }
                    }
                });

        result.writeAsText(outputPath+"result_tuples.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
        //latencies.writeAsText(outputPath+"latency.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
        throughput_messages.writeAsText(outputPath+"throughput.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }


}
