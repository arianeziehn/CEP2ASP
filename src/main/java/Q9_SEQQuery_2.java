import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
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
 * --inputQnV ./src/main/resources/QnV_R2000070.csv
 */

public class Q9_SEQQuery_2 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("inputQnV")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("inputQnV");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        Integer velFilter = parameters.getInt("vel", 107);
        Integer quaFilter = parameters.getInt("qua", 107);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);
        String outputPath;

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ9_ASP2.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input1 = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input1.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input1
                .filter(t -> ((Double) t.getValue()) >= velFilter && (t instanceof VelocityEvent))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = input1
                .filter(t -> ((Double) t.getValue()) >= quaFilter && t instanceof QuantityEvent)
                .map(new UDFs.MapKey());


        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = velStream.join(quaStream)
                .where(new UDFs.getArtificalKey())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    final HashSet<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>(1000);
                    @Override
                    public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple2<>(d1.f0, d2.f0);
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

        result.flatMap(new LatencyLoggerT2(true));
        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
