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
 *--input ./src/main/resources/QnV_R2000070.csv
 */

public class Q8_SEQQueryLS_IntervalJoin {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer sensors = parameters.getInt("sensors",3);
        Integer velFilter = parameters.getInt("vel", 100);
        Integer quaFilter = parameters.getInt("qua", 110);
        Integer windowsize = parameters.getInt("wsize",2);
        long throughput = parameters.getLong("tput",100000);

        String outputPath;
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ8_ASP_LargeScale.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors,",",throughput));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> stream = input
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = stream.filter(t -> ((Double) t.f0.getValue()) > velFilter && (t.f0 instanceof VelocityEvent));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = stream.filter(t -> ((Double) t.f0.getValue()) > quaFilter && t.f0 instanceof QuantityEvent);


        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> joinedStream = velStream.keyBy(new UDFs.getArtificalKey())
                .intervalJoin(quaStream.keyBy(new UDFs.getArtificalKey()))
                .between(Time.seconds(1), Time.seconds((windowsize * 60) - 1))
                .process(new ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {

                    @Override
                    public void processElement(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, ProcessJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple2<>(d1.f0, d2.f0));
                        }
                    }
                });

        joinedStream.flatMap(new LatencyLoggerT2(true));
        joinedStream//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        //System.out.println(env.getExecutionPlan());
        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
