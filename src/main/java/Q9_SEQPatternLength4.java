import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
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
 * --inputQnV ./src/main/resources/QnV_R2000070.csv --inputPM ./src/main/resources/luftdaten_11245.csv
 */

public class Q9_SEQPatternLength4 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("inputQnV")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("inputQnV");
        String file1 = parameters.get("inputPM");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 115);
        Integer quaFilter = parameters.getInt("qua", 105);
        Integer windowSize = parameters.getInt("wsize", 15);
        int patternLength = parameters.getInt("pattern", 4);
        Integer pm2Filter = parameters.getInt("pm2", 5);
        Integer pm10Filter = parameters.getInt("pm10", 5);
        long throughput = parameters.getLong("tput", 100000);
        long tputQnV = (long) (throughput * 0.75);
        long tputPM = (long) (throughput * 0.25);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ9_CEP4.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input1 = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", tputQnV))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> input2 = env.addSource(new KeyedDataPointSourceFunction(file1, iterations, ";", tputPM))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        input1.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputQnV));
        input2.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, tputPM));


        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin(String.valueOf("first")).subtype(VelocityEvent.class).where(
                new SimpleCondition<VelocityEvent>() {
                    @Override
                    public boolean filter(VelocityEvent event) {
                        Double velocity = (Double) event.getValue();
                        return velocity > velFilter;
                    }
                }).followedByAny(String.valueOf("second")).subtype(QuantityEvent.class).where(
                new IterativeCondition<QuantityEvent>() {
                    @Override
                    public boolean filter(QuantityEvent event, Context<QuantityEvent> ctx) throws Exception {
                        Double quantity = (Double) event.getValue();
                        return quantity > quaFilter;
                    }

                });

        if (patternLength >= 3) {
            pattern = pattern
                    .followedByAny(String.valueOf("third")).subtype(PartMatter2Event.class).where(
                            new SimpleCondition<PartMatter2Event>() {
                                @Override
                                public boolean filter(PartMatter2Event event) throws Exception {
                                    Double pm2 = (Double) event.getValue();
                                    return pm2 > pm2Filter;
                                }
                            });
        }
        if (patternLength >= 4) {
            pattern = pattern
                    .followedByAny(String.valueOf("forth")).subtype(PartMatter10Event.class).where(
                            new SimpleCondition<PartMatter10Event>() {
                                @Override
                                public boolean filter(PartMatter10Event event) throws Exception {
                                    Double pm10 = (Double) event.getValue();
                                    return pm10 > pm10Filter;
                                }
                            });
        }

        pattern = pattern.within(Time.minutes(windowSize));

        DataStream<KeyedDataPointGeneral> input = input1.union(input2);
        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);

        if (patternLength == 3) {
            DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple3SEQ());
            result.flatMap(new LatencyLoggerT3(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else if (patternLength == 4) {
            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple4());
            result.flatMap(new LatencyLoggerT4(true));
            result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }

}
