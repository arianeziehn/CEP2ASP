import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import util.*;

import java.util.concurrent.TimeUnit;

public class PNSEQ_E1 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        String outputPath;
        Integer quaFilter = parameters.getInt("qua", 80);
        Integer velFilter = parameters.getInt("vel", 175);
        Integer windowSize = parameters.getInt("wsize", 100);
        Integer selectivity = parameters.getInt("sel", 1); // int between (0 and 100]
        Integer runtimeMinutes = parameters.getInt("run", 1); // time the source generates events in min
        Integer pm10Filter = parameters.getInt("pm10", 30);
        long throughput = parameters.getLong("tput", 100000);
        long tputPerStream = (long) (throughput * 0.34);

        if (!parameters.has("output")) {
            outputPath = "./src/main/resources/Result_PNSEQ_E1.csv";
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputQuantity = env.addSource(new ArtificalSourceFunction("Quantity", tputPerStream, windowSize, runtimeMinutes, 0.2, 0.21, selectivity))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputPM10 = env.addSource(new ArtificalSourceFunction("PM10", tputPerStream, windowSize, runtimeMinutes, 0.6, 0.61, selectivity))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputVelocity = env.addSource(new ArtificalSourceFunction("Velocity", tputPerStream, windowSize, runtimeMinutes, 0.2, 0.61, selectivity, "NEG"))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        inputQuantity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));
        inputPM10.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));
        inputVelocity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPerStream));

        DataStream<KeyedDataPointGeneral> input = inputQuantity.union(inputVelocity).union(inputPM10);

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(QuantityEvent.class).where(
                new SimpleCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event1) {
                        Double quantity = (Double) event1.getValue();
                        return quantity > quaFilter;
                    }
                }).notFollowedBy("not_next").subtype(VelocityEvent.class).where(
                new SimpleCondition<VelocityEvent>() {
                    @Override
                    public boolean filter(VelocityEvent event2) throws Exception {
                        Double vel = (Double) event2.getValue();
                        return vel < velFilter;
                    }
                }
        ).followedByAny("next").subtype(PartMatter10Event.class).where(
                new SimpleCondition<PartMatter10Event>() {
                    @Override
                    public boolean filter(PartMatter10Event event2) throws Exception {
                        Double pm10 = (Double) event2.getValue();
                        return pm10 > pm10Filter;
                    }
                }
        ).within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple2());
        //flatSelect contains the throughputLogger and is required to actual sink the pattern stream
        result.flatMap(new LatencyLoggerT2());
        result //.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
