package Q_SubmissionSigmodVLDB;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import util.*;
import java.util.concurrent.TimeUnit;

/**
 * Run with these parameters:
 * --input ./src/main/resources/QnV.csv
 * Approximate Iteration Solution which presents the number of occurrence per window
 * Note that for accurate solutions an apply function is required that sorts the input and create sequence combinations from the ordered list which can support unbounded iterations patterns
 * if combinations are requested and bounded iterations are used -> use joins
 */

public class Q7_ITERQuery_I2 {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        long throughput = parameters.getLong("tput", 100000);
        int iter = parameters.getInt("iter", 5);
        Integer windowSize = parameters.getInt("wsize", 15);
        Integer velFilter = parameters.getInt("vel", 184);

        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ7_I2_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        // preparation, filter and add key and count column
        DataStream<Tuple3<KeyedDataPointGeneral, Integer, Integer>> velStream = input
                .filter(t -> ((Double) t.getValue()) >= velFilter && (t instanceof VelocityEvent)) // filer non relevant tuples
                .map(new UDFs.MapKey()) // add artificial key = 1 if no key relationship is provided by your data
                .map(new UDFs.MapCount()) // add a count column to the tuple
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestampKeyedDataPointGeneral2Int(60000));


        // Window Sum Function over the defined window size
        DataStream<Tuple3<KeyedDataPointGeneral, Integer, Integer>> aggStream = velStream.keyBy(new KeySelector<Tuple3<KeyedDataPointGeneral, Integer, Integer>, Integer>() { // here we use artificial
                    @Override
                    public Integer getKey(Tuple3<KeyedDataPointGeneral, Integer, Integer> tuple3) throws Exception {
                        return tuple3.f1;
                    }
                })
                // .keyBy(new UDFs.DataKeySelectorTuple2Int()) // if this is select only tuples with same key are considered for a match
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                /**
                 * if your workload requires accurate information of the all sequences contained in a window, use .apply() sort your input iterable and
                 * collect the different sequences as arraylist from the window (multiple outputs are allowed for UDF window functions)

                .apply(new WindowFunction<Tuple3<KeyedDataPointGeneral, Integer, Integer>, Object, Integer, TimeWindow>() {
                    @Override
                    public void apply(Integer integer, TimeWindow timeWindow, Iterable<Tuple3<KeyedDataPointGeneral, Integer, Integer>> iterable, Collector<Object> collector) throws Exception {

                    }
                })
                 * else: we sum the assigned count for all events assigned to the window
                 */
                .sum(2);

        // last, check if any aggregation fulfill the times condition
        aggStream.filter(new FilterFunction<Tuple3<KeyedDataPointGeneral, Integer, Integer>>() {
            @Override
            public boolean filter(Tuple3<KeyedDataPointGeneral, Integer, Integer> tuple2) throws Exception {
                return tuple2.f2 >= iter; // here we are approximate, we report that we detected at least the number of specified events, thus, if > iter we know that the window contains multiple sequences
            }
        }).writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

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
