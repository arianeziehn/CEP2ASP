package hiddenQueries;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import util.*;

import java.util.concurrent.TimeUnit;

/**
 * This class presents an elementary SEQ pattern that searches for a match two streams using our artificial data source
 * to produce constant workload. In particular, our source ensures that given a window size (wsize) and a selectivity
 * (sel) one match is contained in a batch of the windowsize.
 */

public class PSEQ_E1 {
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
            outputPath = "./src/main/resources/Result_PSEQ_E1.csv";
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

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(QuantityEvent.class).where(
                new SimpleCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event1) {
                        Double quantity = (Double) event1.getValue();
                        return quantity > quaFilter;
                    }
                }).followedByAny("next").subtype(PartMatter10Event.class).where(
                new IterativeCondition<PartMatter10Event>() {
                    @Override
                    public boolean filter(PartMatter10Event event2, Context<PartMatter10Event> ctx) throws Exception {
                        Double pm10 = (Double) event2.getValue();
                        if (pm10 > pm10Filter) {
                            double distance = 0.0;
                            for (KeyedDataPointGeneral event : ctx.getEventsForPattern("first")) {
                                distance = UDFs.checkDistance(event, event2);
                                return distance < 10.0;
                            }
                        }
                        return false;
                    }

                }
        ).within(Time.minutes(windowSize));

        DataStream<KeyedDataPointGeneral> input = inputQuantity.union(inputPM10);

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);

        DataStream<Tuple2<KeyedDataPointGeneral,KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple2());

        result.flatMap(new LatencyLoggerT2());
        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
