import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
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
 */

public class Q8_SEQPatternLS {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer sensors = parameters.getInt("sensors",3);
        Integer velFilter = parameters.getInt("vel", 100);
        Integer quaFilter = parameters.getInt("qua", 110);
        Integer windowsize = parameters.getInt("wsize",2);
        long throughput = parameters.getLong("tput",100000);

        String outputPath;
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ8_CEP_LargeScale.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors,",",throughput));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        input = input.assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp()).keyBy(KeyedDataPointGeneral::getKey);

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin("first").subtype(VelocityEvent.class)
                .where(
                        new SimpleCondition<VelocityEvent>() {
                            @Override
                            public boolean filter(VelocityEvent event1) {
                                Double velocity = (Double) event1.getValue();
                                return velocity > velFilter;
                            }
                        }).followedByAny("next").subtype(QuantityEvent.class).where(
                        new SimpleCondition<QuantityEvent>() {
                            @Override
                            public boolean filter(QuantityEvent event2) throws Exception {
                                Double quantity = (Double) event2.getValue();
                                return quantity > quaFilter;
                            }
                        }
                ).within(Time.minutes(windowsize));

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);

        DataStream<Tuple2<KeyedDataPointGeneral,KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple2());

        result.flatMap(new LatencyLoggerT2(true));

        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        //System.out.println(env.getExecutionPlan());
        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
