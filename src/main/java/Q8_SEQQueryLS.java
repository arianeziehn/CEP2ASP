import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
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
 *--input ./src/main/resources/QnV_R2000070.csv
 */

public class Q8_SEQQueryLS {
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
            outputPath = file.replace(".csv", "_resultQ8_ASP_LargeScale.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> input = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors,",",throughput));

        input.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));


        DataStream<KeyedDataPointGeneral> velStream = input
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent)).assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp());

        DataStream<KeyedDataPointGeneral> quaStream = input
                .filter(t -> ((Double) t.getValue()) > quaFilter && t instanceof QuantityEvent)
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp());


        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> joinedStream = velStream.join(quaStream)
                .where(KeyedDataPointGeneral::getKey)
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowsize), Time.minutes(1)))
                .apply(new FlatJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral,KeyedDataPointGeneral>>() {
                    final HashSet<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>(1000);
                    @Override
                    public void join(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if(d1.getTimeStampMs() < d2.getTimeStampMs()){
                            Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple2<>(d1,d2);
                            if (!set.contains(result)) {
                                if (set.size() == 1000) {
                                    set.removeAll(set);
                                    // to maintain the HashSet Size we flush after 1000 entries
                                }
                                collector.collect(result);
                                set.add(result);
                            }
                        }
                    }
                });

        joinedStream.flatMap(new LatencyLoggerT2(true));
        joinedStream//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        //System.out.println(env.getExecutionPlan());
        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
