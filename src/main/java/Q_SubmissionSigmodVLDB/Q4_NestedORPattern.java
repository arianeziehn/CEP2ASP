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
 * Run with these parameters: they have matching event time
 * --input ./src/main/resources/QnV_R2000070.csv --inputAQ ./src/main/resources/luftdaten_11245.csv // default params set for this
 * or
 * --input ./src/main/resources/QnV_R2000073.csv --inputAQ ./src/main/resources/luftdaten_11245.csv
 */

public class Q4_NestedORPattern {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input") || !parameters.has("inputAQ")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String file1 = parameters.get("inputAQ");
        Integer pm10Filter = parameters.getInt("pm10", 30);
        Integer pm2Filter = parameters.getInt("pm2", 30);
        Integer quaFilter = parameters.getInt("qua", 100);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);
        Integer iterations = parameters.getInt("iter",1); // 36 to match 10000000
        String outputPath;
        long tputQnV = 0;
        long tputPM = 0;
        if(throughput > 0){
            tputQnV = (long) (throughput*0.67);
            tputPM = (long) (throughput*0.33);
        }

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ4_CEP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input1 = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", tputQnV))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> input2 = env.addSource(new KeyedDataPointSourceFunction(file1, iterations,";", tputPM))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        input1.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputQnV));
        input2.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPM));

        DataStream<KeyedDataPointGeneral> input = input1.union(input2);

        Pattern<KeyedDataPointGeneral, ?> pattern1 = Pattern.<KeyedDataPointGeneral>begin("first").subtype(QuantityEvent.class).where(
                new SimpleCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event1) {
                        Double quantity = (Double) event1.getValue();
                        return quantity > quaFilter;
                    }
                }).followedByAny("next").subtype(PartMatter10Event.class).where(
                new SimpleCondition<PartMatter10Event>() {
                    @Override
                    public boolean filter(PartMatter10Event event2) throws Exception {
                        Double pm10 = (Double) event2.getValue();
                        return pm10 > pm10Filter;
                    }
                }
        ).within(Time.minutes(windowSize));

        Pattern<KeyedDataPointGeneral, ?> pattern2 = Pattern.<KeyedDataPointGeneral>begin("first").subtype(QuantityEvent.class).where(
                new SimpleCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event1) {
                        Double quantity = (Double) event1.getValue();
                        return quantity > quaFilter;
                    }
                }).followedByAny("next").subtype(PartMatter2Event.class).where(
                new SimpleCondition<PartMatter2Event>() {
                    @Override
                    public boolean filter(PartMatter2Event event2) throws Exception {
                        Double pm2 = (Double) event2.getValue();
                        return pm2 > pm2Filter;
                    }
                }
        ).within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern1);
        PatternStream<KeyedDataPointGeneral> patternStream2 = CEP.pattern(input, pattern2);

        DataStream<String> result = patternStream.flatSelect(new UDFs.GetResultTuple())
                .union(patternStream2.flatSelect(new UDFs.GetResultTuple()));

        result //.print();
              .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
