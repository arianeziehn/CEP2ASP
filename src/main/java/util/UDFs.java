package util;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.Collector;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UDFs {
    public static class ExtractTimestamp implements AssignerWithPeriodicWatermarks<KeyedDataPointGeneral> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(KeyedDataPointGeneral element, long l) {
            long timestamp = element.getTimeStampMs();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static double checkDistance(KeyedDataPointGeneral event, KeyedDataPointGeneral event2) {
        double dx = 71.5 * (event.getLongitude() - event2.getLongitude());
        double dy = 111.3 * (event.getLatitude() - event2.getLatitude());
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    public static class getArtificalKey implements KeySelector<Tuple2<KeyedDataPointGeneral, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple2<KeyedDataPointGeneral, Integer> data) throws Exception {
            return data.f1;
        }
    }

    public static class MapKey implements MapFunction<KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral, Integer>> {
        @Override
        public Tuple2<KeyedDataPointGeneral, Integer> map(KeyedDataPointGeneral dp) throws Exception {
            return new Tuple2<KeyedDataPointGeneral, Integer>(dp, 1);
        }
    }

    public static class MapCount implements MapFunction<Tuple2<KeyedDataPointGeneral, Integer>, Tuple3<KeyedDataPointGeneral, Integer, Integer>> {
        @Override
        public Tuple3<KeyedDataPointGeneral, Integer, Integer> map(Tuple2<KeyedDataPointGeneral, Integer> dp) throws Exception {
            return new Tuple3<KeyedDataPointGeneral, Integer, Integer>(dp.f0, dp.f1, 1);
        }
    }

    public static class GetResultTuple implements PatternFlatSelectFunction<KeyedDataPointGeneral, String> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<String> collector) throws Exception {
            collector.collect(map.toString());
        }
    }

    public static class ExtractTimestampNOT implements AssignerWithPeriodicWatermarks<Tuple3<KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestampNOT() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestampNOT(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple3<KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f0.getTimeStampMs();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestampKeyedDataPointGeneral2Int implements AssignerWithPeriodicWatermarks<Tuple3<KeyedDataPointGeneral, Integer, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestampKeyedDataPointGeneral2Int(){
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestampKeyedDataPointGeneral2Int(long periodMs){
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple3<KeyedDataPointGeneral, Integer,Integer> element, long l) {
            long timestamp = element.f0.getTimeStampMs();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class TimeComparator implements Comparator<Tuple2<KeyedDataPointGeneral, Integer>> {
        @Override
        public int compare(Tuple2<KeyedDataPointGeneral, Integer> t1, Tuple2<KeyedDataPointGeneral, Integer> t2) {
            if (t1.f0.getTimeStampMs() < t2.f0.getTimeStampMs()) return -1;
            if (t1.f0.getTimeStampMs() == t2.f0.getTimeStampMs()) return 0;
            if (t1.f0.getTimeStampMs() > t2.f0.getTimeStampMs()) return 1;
            return 0;
        }
    }

    public static class KeySelectorNASDAQ implements KeySelector<KeyedDataPointNASDAQ, String> {
        @Override
        public String getKey(KeyedDataPointNASDAQ keyedDataPoint) throws Exception {
            return keyedDataPoint.getKey();
        }
    }

    public static class GetResultTupleNASDAQ implements PatternFlatSelectFunction<KeyedDataPointNASDAQ, String> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointNASDAQ>> map, Collector<String> collector) throws Exception {


            collector.collect(map.toString());

        }
    }

    public static class ExtractTimestampNASDAQ extends AscendingTimestampExtractor<KeyedDataPointNASDAQ> {
        private static final long serialVersionUID = 1L;

        @Override
        public long extractAscendingTimestamp(KeyedDataPointNASDAQ element) {
            return element.getTimeStampMs();
        }
    }
}
