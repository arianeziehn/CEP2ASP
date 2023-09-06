import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
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
public class PITER_E1 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 175);
        Integer windowSize = parameters.getInt("wsize", 100);
        Integer selectivity = parameters.getInt("sel", 1); // int between (0 and 100]
        Integer runtimeMinutes = parameters.getInt("run", 1); // time the source generates events in min
        Integer times = parameters.getInt("times", 3);
        selectivity = selectivity * times;
        long throughput = parameters.getLong("tput", 100000);

        if (!parameters.has("output")) {
            outputPath = "./src/main/resources/Result_PITER_E1.csv";
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputVelocity = env.addSource(new ArtificalSourceFunction("Velocity", throughput, windowSize, runtimeMinutes, selectivity, "ITER"))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        inputVelocity.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class).where(
                        new IterativeCondition<VelocityEvent>() {
                            @Override
                            public boolean filter(VelocityEvent event1, Context<VelocityEvent> ctx) throws Exception {
                                if ((Double) event1.getValue() > velFilter) {
                                    for (VelocityEvent event : ctx.getEventsForPattern("first")) {
                                        if ((Double) event1.getValue() < (Double) event.getValue()) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                                return false;
                            }
                        }).times(times).allowCombinations()
                .within(Time.minutes(windowSize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(inputVelocity, pattern);
        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple3());

        result.flatMap(new LatencyLoggerT3());
        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
