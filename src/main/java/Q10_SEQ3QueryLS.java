import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
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
 *--input ./src/main/resources/QnV_R2000070.csv
 */

public class Q10_SEQ3QueryLS {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("inputQnV")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("inputQnV");
        String filePM = parameters.get("inputPM");
        Integer sensors = parameters.getInt("sensors", 16);
        Integer velFilter = parameters.getInt("vel", 78);
        Integer quaFilter = parameters.getInt("qua", 67);
        Integer pm10Filter = parameters.getInt("pmb", 64);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);

        String outputPath;
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ10_ASP_LargeScale.csv");
        } else {
            outputPath = parameters.get("output");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KeyedDataPointGeneral> inputQnV = env.addSource(new KeyedDataPointParallelSourceFunction(file, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(60000));

        DataStream<KeyedDataPointGeneral> inputPM = env.addSource(new KeyedDataPointParallelSourceFunction(filePM, sensors, ",", throughput))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp(180000));

        inputQnV.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));
        inputPM.flatMap(new ThroughputLogger<KeyedDataPointGeneral>(KeyedDataPointParallelSourceFunction.RECORD_SIZE_IN_BYTE, throughput));

        DataStream<KeyedDataPointGeneral> velStream = inputQnV
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent))
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp());

        DataStream<KeyedDataPointGeneral> quaStream = inputQnV
                .filter(t -> ((Double) t.getValue()) > quaFilter && t instanceof QuantityEvent)
                .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp());

        DataStream<KeyedDataPointGeneral> pm10Stream = inputPM
                .filter(t -> ((Double) t.getValue()) > pm10Filter && t instanceof PartMatter10Event);


        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> seq2 = velStream.join(quaStream)
                .where(KeyedDataPointGeneral::getKey)
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<KeyedDataPointGeneral, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>() {
                    final HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> set = new HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>>(1000);
                    @Override
                    public void join(KeyedDataPointGeneral d1, KeyedDataPointGeneral d2, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> collector) throws Exception {
                        if (d1.getTimeStampMs() < d2.getTimeStampMs()) {
                            Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> result = new Tuple3<>(d1, d2, d1.getTimeStampMs());
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
                }) .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLong(60000));

        DataStream<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> seq3 = seq2.join(pm10Stream)
                .where(new UDFs.getKeyT3())
                .equalTo(KeyedDataPointGeneral::getKey)
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    final HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> set = new HashSet<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>>();
                    @Override
                    public void join(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> d1, KeyedDataPointGeneral d2, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f1.getTimeStampMs() < d2.getTimeStampMs()) {
                            Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral> result = new Tuple3<>(d1.f0, d1.f1, d2);
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

        seq3.flatMap(new LatencyLoggerT3());
        seq3//.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        //System.out.println(env.getExecutionPlan());
        JobExecutionResult executionResult = env.execute("My Flink Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
