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
 * This class presents an elementary SEQ pattern translated into a join that searches for a match two streams using our artificial data source
 * to produce constant workload. In particular, our source ensures that given a window size (wsize) and a selectivity
 * (sel) one match is contained in a batch of the windowsize.
 */

public class QSEQ_E1_IntervalJoin {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        String outputPath;
        Integer quaFilter = parameters.getInt("qua", 80);
        Integer windowSize = parameters.getInt("wsize", 100);
        Integer selectivity = parameters.getInt("sel", 1); // int between (0 and 100]
        Integer runtimeMinutes = parameters.getInt("run", 1); // time the source generates events in min
        Integer pm10Filter = parameters.getInt("pm10", 30);
        long throughput = parameters.getLong("tput", 100000);
        long tputPerStream = (long) (throughput * 0.5);

        if (!parameters.has("output")) {
            outputPath = "./src/main/resources/Result_QSEQ_E1_IntervalJoin.csv";
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputQuantity = env.addSource(new ArtificalSourceFunction("Quantity", tputPerStream, windowSize, runtimeMinutes, 0.0, 0.5, selectivity))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputPM10 = env.addSource(new ArtificalSourceFunction("PM10", tputPerStream, windowSize, runtimeMinutes, 0.5, 1.0, selectivity))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        inputQuantity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));
        inputPM10.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));

        DataStream<KeyedDataPointGeneral> quaStream = inputQuantity.filter(t -> ((Double) t.getValue()) > quaFilter);
        DataStream<KeyedDataPointGeneral> pm10Stream = inputPM10.filter(t -> ((Double) t.getValue()) > pm10Filter);

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = quaStream.keyBy(KeyedDataPointGeneral::getKey)
                .intervalJoin(pm10Stream.keyBy(KeyedDataPointGeneral::getKey))
                .between(Time.seconds(1), Time.seconds(windowSize*60 - 1))
                .process(new ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void processElement(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, ProcessJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>.Context context, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {

                            double distance = UDFs.checkDistance(d1, d2);
                            if (distance < 10.0) {
                                Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple2<>(d1, d2);
                                collector.collect(result);
                            }

                    }
                });

        result.flatMap(new LatencyLoggerT2());
        result //.print();
              .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
