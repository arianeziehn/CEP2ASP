import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
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
 * --input ./src/main/resources/QnV.csv
 */

public class Q1_SEQQuery_IntervalJoin {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer velFilter = parameters.getInt("vel", 175);
        Integer quaFilter = parameters.getInt("qua", 250);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);

        String outputPath;
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ1_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> stream = input
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = stream.filter(t -> ((Double) t.f0.getValue()) > velFilter && (t.f0 instanceof VelocityEvent));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = stream.filter(t -> ((Double) t.f0.getValue()) > quaFilter && t.f0 instanceof QuantityEvent);

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = velStream.keyBy(new UDFs.getArtificalKey())
                .intervalJoin(quaStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowSize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {

                    @Override
                    public void processElement(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        double distance = UDFs.checkDistance(d1.f0, d2.f0);
                        if (distance < 10.0) {
                            collector.collect(new Tuple2<>(d1.f0, d2.f0));
                        }
                    }
                });

        result.flatMap(new LatencyLoggerT2());
        result//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
