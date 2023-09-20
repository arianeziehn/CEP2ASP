import org.apache.flink.api.common.JobExecutionResult;
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

/**
 * Run with these parameters:
 * --inputQnV ./src/main/resources/QnV_R2000070.csv --inputPM ./src/main/resources/luftdaten_11245.csv
 * --inputQnV ./src/main/resources/QnV_R2000070_integrated.csv --inputPM ./src/main/resources/luftdaten_11245_integrated.csv
 */

public class Q10_SEQ3PatternLS {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("inputQnV")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("inputQnV");
        String filePM = parameters.get("inputPM");
        Integer sensors = parameters.getInt("sensors", 16);
        Integer velFilter = parameters.getInt("vel", 78);
        Integer quaFilter = parameters.getInt("qua", 67);
        Integer pm10Filter = parameters.getInt("pmb", 64);
        Integer windowsize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 10000);

        String outputPath;
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ10_CEP_LargeScale.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputQnV = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputPM = env.addSource(new KeyedDataPointParallelSourceFunction(filePM, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        inputQnV.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));
        inputPM.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class)
                .where(
                        new SimpleCondition<VelocityEvent>() {
                            @Override
                            public boolean filter(VelocityEvent event1) {
                                Double velocity = (Double) event1.getValue();
                                return velocity > velFilter;
                            }
                        }).followedByAny("second").subtype(QuantityEvent.class).where(
                        new SimpleCondition<QuantityEvent>() {
                            @Override
                            public boolean filter(QuantityEvent event2) throws Exception {
                                Double quantity = (Double) event2.getValue();
                                return quantity > quaFilter;
                            }
                        }).followedByAny("third").subtype(PartMatter10Event.class).where(
                        new SimpleCondition<PartMatter10Event>() {
                            @Override
                            public boolean filter(PartMatter10Event event3) throws Exception {
                                Double pm10 = (Double) event3.getValue();
                                return pm10 > pm10Filter;
                            }
                        }).within(Time.minutes(windowsize));

        DataStream<KeyedDataPointGeneral> input = inputQnV.union(inputPM).keyBy(KeyedDataPointGeneral::getKey);

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);

        DataStream<Tuple3<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple3());

        //result.flatMap(new LatencyLoggerT3());
        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        //System.out.println(env.getExecutionPlan());
        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
