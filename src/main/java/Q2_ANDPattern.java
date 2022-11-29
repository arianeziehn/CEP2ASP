import org.apache.flink.api.common.JobExecutionResult;
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
 * Run with these parameters:
 * --input ./src/main/resources/QnV_large.csv
 */

public class Q2_ANDPattern {
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
            outputPath = file.replace(".csv", "_resultQ2_CEP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000)); // indicate the time field for the matching process
        // .keyBy(new UDFs.DataKeySelector()); // if this is select only tuples with same key (i.e., same sensor id) match

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern1 = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class).where(
                new SimpleCondition<VelocityEvent>() {
                    @Override
                    public boolean filter(VelocityEvent event1) {
                        Double velocity = (Double) event1.getValue();
                        return velocity > velFilter;
                    }
                }).followedByAny("next").subtype(QuantityEvent.class).where(
                new IterativeCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event2, Context<QuantityEvent> ctx) throws Exception {
                        Double quantity = (Double) event2.getValue();

                        if (quantity > quaFilter) {
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

        Pattern<KeyedDataPointGeneral, ?> pattern2 = Pattern.<KeyedDataPointGeneral>begin("first").subtype(QuantityEvent.class).where(
                new SimpleCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event2) throws Exception {
                        Double quantity = (Double) event2.getValue();
                        return quantity > quaFilter;
                    }
                }
        ).followedByAny("next").subtype(VelocityEvent.class).where(
                new IterativeCondition<VelocityEvent>() {
                    @Override
                    public boolean filter(VelocityEvent event1, Context<VelocityEvent> ctx) throws Exception {
                        Double velocity = (Double) event1.getValue();
                        if (velocity > velFilter) {
                            double distance = 0.0;
                            for (KeyedDataPointGeneral event : ctx.getEventsForPattern("first")) {
                                distance = UDFs.checkDistance(event, event1);
                                return distance < 10.0;
                            }
                        }
                        return false;
                    }
                }).within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream1 = CEP.pattern(input, pattern1);
        PatternStream<KeyedDataPointGeneral> patternStream2 = CEP.pattern(input, pattern2);

        /**
         * As Flink does not provide the conjunction operator, i.e., and between two different subtypes, we create to sequence pattern
         * and unify their results.
         * */
        DataStream<String> result = patternStream1.flatSelect(new UDFs.GetResultTuple())
                .union(patternStream2.flatSelect(new UDFs.GetResultTuple()));

        result//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
