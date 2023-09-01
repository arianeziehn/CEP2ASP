package Q_SubmissionSigmodVLDB;

import org.apache.flink.api.common.JobExecutionResult;
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

/**
 * Run with these parameters:
 * --input ./src/main/resources/QnV.csv
 */

public class Q3_ORPattern {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 245);
        Integer quaFilter = parameters.getInt("qua", 280);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ3_CEP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern1 = Pattern.<KeyedDataPointGeneral>begin("first").subtype(QuantityEvent.class).where(
                new SimpleCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event1) {
                        Double quantity = (Double) event1.getValue();
                        return quantity > quaFilter;
                    }
                }).within(Time.minutes(windowSize));

        Pattern<KeyedDataPointGeneral, ?> pattern2 = Pattern.<KeyedDataPointGeneral>begin("second").subtype(VelocityEvent.class).where(
                new SimpleCondition<VelocityEvent>() {
                    @Override
                    public boolean filter(VelocityEvent event2) throws Exception {
                        Double vel = (Double) event2.getValue();
                        return vel > velFilter;
                    }
                }).within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern1);
        PatternStream<KeyedDataPointGeneral> patternStream2 = CEP.pattern(input, pattern2);

        /**
         * We can only apply one pattern to the stream and not use .or() for different subtypes (event types)
         * thus, matches from patternStream and patternStream2 are not related.
         * Below, is just composing results of both entries at the end.
         * */
        DataStream<String> result = patternStream.flatSelect(new UDFs.GetResultTuple())
                .union(patternStream2.flatSelect(new UDFs.GetResultTuple()));

        result //.print();
              .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
