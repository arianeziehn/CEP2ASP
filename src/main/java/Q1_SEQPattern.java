import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
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
 * --input ./src/main/resources/QnV.csv
 */

public class Q1_SEQPattern {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer velFilter = parameters.getInt("vel",175);
        Integer quaFilter = parameters.getInt("qua",250);
        Integer windowSize = parameters.getInt("wsize",15);
        long throughput = parameters.getLong("tput",100000);

        String outputPath;
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ1_CEP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000)); // indicate the time field for the matching process
               // .keyBy(new UDFs.DataKeySelector()); // if this is select only tuples with same key (i.e., same sensor id) match

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class).where(
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
                        if (quantity > quaFilter){
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

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);

        DataStream<Tuple2<KeyedDataPointGeneral,KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple2());

        result.flatMap(new LatencyLoggerT2());
        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}