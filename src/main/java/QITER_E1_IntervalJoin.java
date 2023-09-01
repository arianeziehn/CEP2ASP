import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple3;
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

public class QITER_E1_IntervalJoin {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 175);
        Integer windowSize = parameters.getInt("wsize", 100);
        Integer selectivity = parameters.getInt("sel", 1); // int between (0 and 100]
        Integer runtimeMinutes = parameters.getInt("run", 1); // time the source generates events in min
        Integer times = parameters.getInt("times", 3);
        selectivity = selectivity * times;
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = "./src/main/resources/Result_QITER_E1_IntervalJoin.csv";
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputVelocity = env.addSource(new ArtificalSourceFunction("Velocity", throughput, windowSize, runtimeMinutes, selectivity, "ITER")).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        inputVelocity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> velStream = inputVelocity.filter(t -> ((Double) t.getValue()) > velFilter);

        // iter2
        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> it2 = velStream.keyBy(KeyedDataPointGeneral::getKey).intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey)).between(Time.minutes(0), Time.minutes(windowSize - 1)).process(new ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
            @Override
            public void processElement(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>.Context context, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                if (d1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.getValue() < (Double) d2.getValue()) {
                    collector.collect(new Tuple3<>(d1, d2, d1.getTimeStampMs()));
                }
            }
        }).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLong(60000));


        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> it3 = it2.keyBy(new UDFs.getKeyT3()).intervalJoin(velStream.keyBy(KeyedDataPointGeneral::getKey)).between(Time.minutes(0), Time.minutes(windowSize - 1)).process(new ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
            @Override
            public void processElement(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, ProcessJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                if (d1.f1.getTimeStampMs() < d2.getTimeStampMs() && (Double) d1.f1.getValue() < (Double) d2.getValue()) {
                    collector.collect(new Tuple3<>(d1.f0, d1.f1, d2));
                }
            }
        });

        it3.flatMap(new LatencyLogger());
        it3//.print();
             .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}

