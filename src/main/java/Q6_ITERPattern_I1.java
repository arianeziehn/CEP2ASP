import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.api.java.tuple.Tuple9;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
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
 * This CEP pattern uses the FlinkCEP times operator and applies and inter-event condition, i.e., increasing values over time
 */
public class Q6_ITERPattern_I1 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 156);
        Integer windowSize = parameters.getInt("wsize", 15);
        int times = parameters.getInt("times", 9);
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ6_I1_CEP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class).where(
                        new IterativeCondition<VelocityEvent>() {
                            @Override
                            public boolean filter(VelocityEvent event1, Context<VelocityEvent> ctx) throws Exception {
                                if ((Double) event1.getValue() < velFilter) {
                                    return false;
                                }
                                for (VelocityEvent event : ctx.getEventsForPattern("first")) {
                                    if ((Double) event1.getValue() < (Double) event.getValue()) {
                                        return false;
                                    }
                                }
                                return true;
                            }
                        }).times(times).allowCombinations()
                .within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);
        // we require a type specific flatmap for our Latency Logging
        if (times == 2) {
            DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple2());
            result.flatMap(new LatencyLoggerT2(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (times == 3) {
            DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple3());
            result.flatMap(new LatencyLoggerT3(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (times == 4 || times == 5 || times > 9) {
            /**
             * TODO
             */
        } else if (times == 6) {
            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple6());
            result.flatMap(new LatencyLoggerT6(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (times == 9) {
            DataStream<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple9());
            result.flatMap(new LatencyLoggerT9(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
