import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import util.*;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Run with these parameters:
 * --input ./src/main/resources/QnV_R2000070.csv
 * Approximate Iteration Optimization which presents the number of occurrence per window
 * Note that for accurate solutions an apply function is required that sorts the input and create sequence combinations from the ordered list which can support unbounded iterations patterns
 * if combinations are requested and bounded iterations are used -> use join mappings (I)
 */

public class Q11_ITERQuery_I2 {
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
        long throughput = parameters.getLong("tput", 100000);
        int iter = parameters.getInt("iter", 4);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ11_I1T3_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent))// filer non relevant tuples
                .map(new UDFs.MapCount_keyed()) // add a count column to the tuple
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestampKeyedDataPointGeneral1Int(60000));

        // Window Sum Function over the defined window size
        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> aggStream = velStream.keyBy(new KeySelector<Tuple2<KeyedDataPointGeneral, Integer>, String>() { // here we use artificial
                    @Override
                    public String getKey(Tuple2<KeyedDataPointGeneral, Integer> t) throws Exception {
                        return t.f0.getKey();
                    }
                })
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                /**
                 * if your workload requires accurate information of the all sequences contained in a window, use .apply() sort your input iterable and
                 * collect the different sequences as arraylist from the window (multiple outputs are allowed for UDF window functions)

                 .apply(new WindowFunction<Tuple3<KeyedDataPointGeneral, Integer, Integer>, Object, Integer, TimeWindow>() {
                @Override public void apply(Integer integer, TimeWindow timeWindow, Iterable<Tuple3<KeyedDataPointGeneral, Integer, Integer>> iterable, Collector<Object> collector) throws Exception {

                }
                })
                 * else: we sum the assigned count for all events assigned to the window
                 */
                .sum(1);


        // last, check if any aggregation fulfill the times condition
        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> result = aggStream
                .flatMap(new FlatMapFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>>() {
            final HashSet<Tuple2<KeyedDataPointGeneral, Integer>> set = new HashSet<Tuple2<KeyedDataPointGeneral, Integer>>(1000);
            // catch duplicates
            @Override
            public void flatMap(Tuple2<KeyedDataPointGeneral, Integer> t, Collector<Tuple2<KeyedDataPointGeneral, Integer>> collector) throws Exception {

                if (t.f1 >= iter) { // here we are approximate, we report that we detected at least the number of specified events, thus, if > iter we know that the window contains multiple sequences
                    Tuple2<KeyedDataPointGeneral, Integer> result = new Tuple2<>(t.f0, t.f1);
                    if (!set.contains(result)) {
                        collector.collect(result);
                        if (set.size() == 1000) {
                            set.removeAll(set);
                            // to maintain the HashSet Size we flush after 1000 entries
                        }
                        set.add(result);
                    }
                }
            }
        });

        //result.flatMap(new LatencyLoggerT2_O3(true));
        result.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        /** Output example:
         *  (Wed Jan 16 12:25:00 CET 2019,R2001396,198.44444444444446, velocity, POINT(8.82536, 50.609455),1,5) -> a single tuple per window with (here) the sum of 5
         *  the output points out that an event occurred, but no details about each any every involved tuple, if this is required use I2
         *  Also we use >= as CEP create multiple sequences if, e.g., 6 events would appear in an interval :
         *  input: e1, e2, e3, e4, e5, e6 may lead to the matches e1, e2, e3, e4, e5 and e1, e2, e3, e4, e6 in CEP (exact combinations depend on the exact time stamps)
         *  ASP will only indicate (Wed Jan 16 12:25:00 CET 2019,R2001396,198.44444444444446, velocity, POINT(8.82536, 50.609455),1,6)
         *  if combination of sequences are required and cannot be archived by keys, write an apply function (UDF), sort your input and generate sequences from the sorted input whenever the sum is larger than the times conditions
         */

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }
}
