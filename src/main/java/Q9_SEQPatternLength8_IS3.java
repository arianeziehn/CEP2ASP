import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple8;
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

public class Q9_SEQPatternLength8_IS {
    public static void main(String[] args) throws Exception {

        String className = "Q9_SEQPatternLength8_IS";

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        Integer iterations = parameters.getInt("iter", 1); // 28 to match 10000000
        Integer velFilter = parameters.getInt("vel", 150);
        Integer quaFilter = parameters.getInt("qua", 250);
        Integer windowSize = parameters.getInt("wsize", 30);
        Integer pm2Filter = parameters.getInt("pm2", 250);
        Integer pm10Filter = parameters.getInt("pm10", 250);
        Integer tempFilter = parameters.getInt("temp", 250);
        Integer humFilter = parameters.getInt("hum", 250);
        Integer var7Filter = parameters.getInt("var7", 250);
        Integer var8Filter = parameters.getInt("var8", 250);
        long throughput = parameters.getLong("tput", 100000);
        Integer file_loops = parameters.getInt("file_loops", 1);


        String outputName = className+"/throughput_"+throughput+"_loop_"+file_loops+"/";
        String outputPath;
        if (parameters.has("output")) {
            outputPath = parameters.get("output") + outputName;
        } else {
            outputPath = "./out/" + outputName;
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, iterations, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        /*needs to be fixed for length8, taken straight from seq2
        DataStream<String> throughput_messages = input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE,
                className, velFilter, quaFilter, windowSize, throughput, file_loops));*/

        Pattern<KeyedDataPointGeneral, ?> pattern = Pattern.<KeyedDataPointGeneral>begin(String.valueOf("first")).subtype(VelocityEvent.class).where(
                        new SimpleCondition<VelocityEvent>() {
                            @Override
                            public boolean filter(VelocityEvent event1) {
                                Double velocity = (Double) event1.getValue();
                                return velocity > velFilter;
                            }
                        }).followedByAny(String.valueOf("second")).subtype(QuantityEvent.class).where(
                        new IterativeCondition<QuantityEvent>() {
                            @Override
                            public boolean filter(QuantityEvent event2, Context<QuantityEvent> ctx) throws Exception {
                                Double quantity = (Double) event2.getValue();
                                return quantity > quaFilter;
                            }

                        }).followedByAny(String.valueOf("third")).subtype(PartMatter2Event.class).where(
                        new SimpleCondition<PartMatter2Event>() {
                            @Override
                            public boolean filter(PartMatter2Event event2) throws Exception {
                                Double pm2 = (Double) event2.getValue();
                                return pm2 > pm2Filter;
                            }
                        })
                        .followedByAny(String.valueOf("forth")).subtype(PartMatter10Event.class).where(
                        new SimpleCondition<PartMatter10Event>() {
                            @Override
                            public boolean filter(PartMatter10Event event10) throws Exception {
                                Double pm10 = (Double) event10.getValue();
                                return pm10 > pm10Filter;
                            }
                        })
                        .followedByAny(String.valueOf("fifth")).subtype(TemperatureEvent.class).where(
                        new SimpleCondition<TemperatureEvent>() {
                            @Override
                            public boolean filter(TemperatureEvent eventTemp) throws Exception {
                                Double temp = (Double) eventTemp.getValue();
                                return temp > tempFilter;
                            }
                        })
                        .followedByAny(String.valueOf("sixth")).subtype(HumidityEvent.class).where(
                        new SimpleCondition<HumidityEvent>() {
                            @Override
                            public boolean filter(HumidityEvent event10) throws Exception {
                                Double hum = (Double) event10.getValue();
                                return hum > humFilter;
                            }
                        })
                        .followedByAny(String.valueOf("seventh")).subtype(Var7Event.class).where(
                        new SimpleCondition<Var7Event>() {
                            @Override
                            public boolean filter(Var7Event event10) throws Exception {
                                Double var7 = (Double) event10.getValue();
                                return var7 > var7Filter;
                            }
                        })
                        .followedByAny(String.valueOf("eighth")).subtype(Var8Event.class).where(
                        new SimpleCondition<Var8Event>() {
                            @Override
                            public boolean filter(Var8Event event10) throws Exception {
                                Double var8 = (Double) event10.getValue();
                                return var8 > var8Filter;
                            }
                        });


        pattern = pattern.within(Time.minutes(windowSize));

        //DataStream<KeyedDataPointGeneral> input = input1.union(input2).union(input3);

        PatternStream<KeyedDataPointGeneral> patternStream = CEP.pattern(input, pattern);
        DataStream<Tuple8<KeyedDataPointGeneral,KeyedDataPointGeneral,KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> result = patternStream.flatSelect(new UDFs.GetResultTuple8());
        result.flatMap(new LatencyLoggerT8(true));

        result.writeAsText(outputPath+"result_tuples.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
        //latencies.writeAsText(outputPath+"latency.csv", FileSystem.WriteMode.OVERWRITE);
        //throughput_messages.writeAsText(outputPath+"throughput.csv", FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
