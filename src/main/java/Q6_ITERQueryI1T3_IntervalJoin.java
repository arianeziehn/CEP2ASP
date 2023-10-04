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
 * Run with these parameters
 * --input ./src/main/resources/QnV.csv
 */

public class Q6_ITERQueryI1T3_IntervalJoin {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        long throughput = parameters.getLong("tput", 0);
        int times = parameters.getInt("times", 3);
        Integer velFilter = parameters.getInt("vel", 205);
        Integer windowSize = parameters.getInt("wsize", 15);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ6_I1T3_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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

        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it3 = it2.keyBy(new UDFs.getKeyT3())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds((windowSize*60)-1))
                .process(new ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void processElement(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f1.getValue() < (Double) d2.getValue()) {
                            collector.collect(new Tuple3<>(d1.f0, d1.f1, d2));
                        }
                    }
                });

        it3.flatMap(new LatencyLoggerT3());
        it3//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}

