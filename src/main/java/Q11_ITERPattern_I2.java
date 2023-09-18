import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.*;
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
 * --input ./src/main/resources/QnV_R2000070.csv
 * Iteration Pattern with simple filter condition
 */

public class Q11_ITERPattern_I2 {
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
        int iter = parameters.getInt("iter", 4);
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ11_I2_CEP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class).where(
                        new SimpleCondition<VelocityEvent>() {
                            @Override
                            public boolean filter(VelocityEvent event) throws Exception {
                                if ((Double) event.getValue() > velFilter) {
                                    return true;
                                }
                                return false;
                            }
                        }).times(iter).allowCombinations()
                .within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input.keyBy(KeyedDataPointGeneral::getKey), pattern);
        // we require a type specific flatmap for our Latency Logging
        if (iter == 2) {
            DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple2());
            result.flatMap(new LatencyLoggerT2(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (iter == 3) {
            DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple3());
            result.flatMap(new LatencyLoggerT3(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (iter == 4 ) {
            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple4());
            //result.flatMap(new LatencyLoggerT4());
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (iter == 6) {
            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple6());
            result.flatMap(new LatencyLoggerT6(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (iter == 9) {
            DataStream<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple9());
            result.flatMap(new LatencyLoggerT9(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }else{
            //TODO
        }

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }

}
