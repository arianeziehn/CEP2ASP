import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import util.*;
import java.util.concurrent.TimeUnit;

/**
 * Run with these parameters: they have matching event time
 * --input ./src/main/resources/QnV.csv
 */

public class Q3_ORQuery {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String outputPath;
        Integer velFilter = parameters.getInt("vel", 245);
        Integer quaFilter = parameters.getInt("qua", 280);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ3_ASP.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointSourceFunction(file, throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> quaStream = input.filter(t -> ((Double) t.getValue()) > quaFilter && (t instanceof QuantityEvent));

        DataStream<KeyedDataPointGeneral> velStream = input.filter(t -> ((Double) t.getValue()) > velFilter && t instanceof VelocityEvent);

        DataStream<KeyedDataPointGeneral> result = quaStream.union(velStream)
                // window is not required for union, if applied a .apply() function is necessary
                .windowAll(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new AllWindowFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, TimeWindow>() {
                    @Override
                    public void apply(TimeWindow timeWindow, Iterable<KeyedDataPointGeneral> iterable, Collector<KeyedDataPointGeneral> collector) throws Exception {
                        iterable.forEach(t -> collector.collect(t));
                    }
                });

        result //.print();
          .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");

    }


}
