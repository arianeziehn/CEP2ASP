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
import java.util.concurrent.TimeUnit;

/**
 * Run with these parameters: they have matching event time
 * --input ./src/main/resources/QnV_R2000070.csv --inputAQ ./src/main/resources/luftdaten_11245.csv
 * or
 * --input ./src/main/resources/QnV_R2000073.csv --inputAQ ./src/main/resources/luftdaten_11245.csv
 */

public class Q4_NestedORQuery {
    public static void main(String[] args) throws Exception {

        final ParameterTool parameters = ParameterTool.fromArgs(args);
        // Checking input parameters
        if (!parameters.has("input") || !parameters.has("inputAQ")) {
            throw new Exception("Input Data is not specified");
        }

        String file = parameters.get("input");
        String file1 = parameters.get("inputAQ");
        Integer pm10Filter = parameters.getInt("pm10", 30);
        Integer pm2Filter = parameters.getInt("pm2", 30);
        Integer quaFilter = parameters.getInt("qua", 100);
        Integer windowSize = parameters.getInt("wsize", 15);
        long throughput = parameters.getLong("tput", 100000);
        Integer iterations = Integer.parseInt(parameters.get("iter", "1"));
        String outputPath;
        long tputQnV = 0;
        long tputPM = 0;
        if (throughput > 0) {
            tputQnV = (long) (throughput * 0.67);
            tputPM = (long) (throughput * 0.33);
        }
        if (!parameters.has("output")) {
            outputPath = file.replace(".csv", "_resultQ4_ASP.csv");
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

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = input1.filter(t -> ((Double) t.getValue()) > quaFilter && (t instanceof QuantityEvent)).map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> PM10Stream = input2.filter(t -> ((Double) t.getValue()) > pm10Filter && t instanceof PartMatter10Event).map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> PM2Stream = input2.filter(t -> ((Double) t.getValue()) > pm2Filter && t instanceof PartMatter2Event).map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result = quaStream.join(PM10Stream)
                .where(new UDFs.getArtificalKey())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple2<>(d1.f0, d2.f0));
                        }
                    }
                });

        DataStream<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> result2 = quaStream.join(PM2Stream)
                .where(new UDFs.getArtificalKey())
                .equalTo(new UDFs.getArtificalKey())
                .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>>() {
                    @Override
                    public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
                        if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                            collector.collect(new Tuple2<>(d1.f0, d2.f0));
                        }
                    }
                });

        result.union(result2) //.print();
                .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }
}
