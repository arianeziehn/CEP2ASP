package Q_SubmissionSigmodVLDB;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.tuple.Tuple6;
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
 * Run with these parameters:
 * --inputQnV ./src/main/resources/QnV_R2000070.csv --inputPM ./src/main/resources/luftdaten_11245.csv
 */

public class Q9_SEQQueryLength4 {
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
            outputPath = file.replace(".csv", "_resultQ9_ASP4.csv");
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


        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> velStream = input1
                .filter(t -> ((Double) t.getValue()) > velFilter && (t instanceof VelocityEvent))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> quaStream = input1
                .filter(t -> ((Double) t.getValue()) > quaFilter && t instanceof QuantityEvent)
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm2Stream = input2
                .filter(t -> ((Double) t.getValue()) > pm2Filter && (t instanceof PartMatter2Event))
                .map(new UDFs.MapKey());

        DataStream<Tuple2<KeyedDataPointGeneral, Integer>> pm10Stream = input2
                .filter(t -> ((Double) t.getValue()) > pm10Filter && t instanceof PartMatter10Event)
                .map(new UDFs.MapKey());

        if (patternLength == 3) {
            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it1 = velStream.join(quaStream)
                    .where(new UDFs.getArtificalKey())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                            }
                        }
                    })
                    .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> result = it1.join(pm2Stream)
                    .where(new UDFs.getArtificalKeyT4())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                            }
                        }
                    });

            result //.print();
                    .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);

        } else if (patternLength == 4) {
            DataStream<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it1 = velStream.join(quaStream)
                    .where(new UDFs.getArtificalKey())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple2<KeyedDataPointGeneral, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f0.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple4<>(d1.f0, d2.f0, d1.f0.getTimeStampMs(), 1));
                            }
                        }
                    })
                    .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp2KeyedDataPointGeneralLongInt(60000));

            DataStream<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> it2 = it1.join(pm2Stream)
                    .where(new UDFs.getArtificalKeyT4())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f1.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple5<>(d1.f0, d1.f1, d2.f0, d1.f2, 1));
                            }
                        }
                    })
                    .assignTimestampsAndWatermarks(new UDFs.ExtractTimestamp3KeyedDataPointGeneralLongInt());

            DataStream<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> result = it2.join(pm10Stream)
                    .where(new UDFs.getArtificalKeyT5())
                    .equalTo(new UDFs.getArtificalKey())
                    .window(SlidingEventTimeWindows.of(Time.minutes(windowSize), Time.minutes(1)))
                    .apply(new FlatJoinFunction<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Tuple2<KeyedDataPointGeneral, Integer>, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>>() {
                        @Override
                        public void join(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> d1, Tuple2<KeyedDataPointGeneral, Integer> d2, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> collector) throws Exception {
                            if (d1.f2.getTimeStampMs() < d2.f0.getTimeStampMs()) {
                                collector.collect(new Tuple6<>(d1.f0, d1.f1, d1.f2, d2.f0, d1.f3, 1));
                            }
                        }
                    });

            result //.print();
                    .writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        }

        JobExecutionResult executionResult = env.execute("My FlinkASP Job");
        System.out.println("The job took " + executionResult.getNetRuntime(TimeUnit.MILLISECONDS) + "ms to execute");
    }

}
