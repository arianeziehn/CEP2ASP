import org.apache.flink.api.common.JobExecutionResult;
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
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import util.*;

import java.util.concurrent.TimeUnit;

/**
 * Run with these parameters
 * --input ./src/main/resources/QnV_R2000070.csv
 */

public class Q11_ITERQuery_I1_4_IVJ {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 114);
        Integer sensors = parameters.getInt("sensors", 16);
        Integer windowSize = parameters.getInt("wsize", 90);
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ11_I1T3_ASP_IVJ.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> velStream = input
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent));

        // iter2
        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it2 = velStream.keyBy(KeyedDataPointGeneral::getKey)
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    @Override
                    public void processElement(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>.Context context, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.getTimeStampMs() < d2.getTimeStampMs()) {
                            collector.collect(new Tuple3<>(d1, d2, d1.getTimeStampMs()));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLong(60000));

        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it3 = it2
                .keyBy(new UDFs.getKeyT3())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    @Override
                    public void processElement(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.getTimeStampMs()) {
                            collector.collect(new Tuple4<>(d1.f0, d1.f1, d2, d1.f2));
                        }
                    }
                }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLong(60000));

        DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it4 = it3.keyBy(new UDFs.getKeyT4())
                .intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.minutes(0), Time.minutes(windowSize))
                .lowerBoundExclusive()
                .upperBoundExclusive()
                .process(new ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void processElement(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f2.getTimeStampMs() < d2.getTimeStampMs()) {
                            collector.collect(new Tuple4<>(d1.f0, d1.f1, d1.f2, d2));
                        }
                    }
                });

        //it4.flatMap(new LatencyLoggerT4(true));
        it4//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}

