package util;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.*;
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

    public static class getOriginalKey implements KeySelector<Tuple2<KeyedDataPointGeneral, Integer>, String> {
        @Override
        public String getKey(Tuple2<KeyedDataPointGeneral, Integer> data) throws Exception {
            return data.f0.getKey();
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

    public static class GetResultTuple3 implements PatternFlatSelectFunction<KeyedDataPointGeneral, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
            if (map.get("first").size() == 1) {
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("second").get(0);
                KeyedDataPointGeneral d3 = map.get("third").get(0);
                collector.collect(new Tuple3<>(d1, d2, d3));
            } else if (map.get("first").size() == 2) { // get result of iteration pattern
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("first").get(1);
                KeyedDataPointGeneral d3 = map.get("first").get(2);
                collector.collect(new Tuple3<>(d1, d2, d3));
            }
        }
    }

    public static class GetResultTuple2 implements PatternFlatSelectFunction<KeyedDataPointGeneral, Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
            if (map.get("first").size() == 1) {
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("next").get(0);
                collector.collect(new Tuple2<>(d1, d2));
            } else if (map.get("first").size() == 2) { // get result of iteration pattern
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("first").get(1);
                collector.collect(new Tuple2<>(d1, d2));
            }
        }
    }
    public static class GetResultTuple4 implements PatternFlatSelectFunction<KeyedDataPointGeneral, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
            if (map.get("first").size() == 1) {
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("second").get(0);
                KeyedDataPointGeneral d3 = map.get("third").get(0);
                KeyedDataPointGeneral d4 = map.get("fourth").get(0);
                collector.collect(new Tuple4<>(d1, d2, d3, d4));
            } else if (map.get("first").size() == 4) { // get result of iteration pattern
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("first").get(1);
                KeyedDataPointGeneral d3 = map.get("first").get(2);
                KeyedDataPointGeneral d4 = map.get("first").get(3);
                collector.collect(new Tuple4<>(d1, d2, d3, d4));
            }
        }
    }

    public static class GetResultTuple5 implements PatternFlatSelectFunction<KeyedDataPointGeneral, Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
            KeyedDataPointGeneral d1 = map.get("first").get(0);
            KeyedDataPointGeneral d2 = map.get("second").get(0);
            KeyedDataPointGeneral d3 = map.get("third").get(0);
            KeyedDataPointGeneral d4 = map.get("fourth").get(0);
            KeyedDataPointGeneral d5 = map.get("fifth").get(0);
            collector.collect(new Tuple5<>(d1, d2, d3, d4, d5));
        }
    }

    public static class GetResultTuple6 implements PatternFlatSelectFunction<KeyedDataPointGeneral, Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
            if (map.get("first").size() == 1) { // get result of sequence pattern
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("second").get(0);
                KeyedDataPointGeneral d3 = map.get("third").get(0);
                KeyedDataPointGeneral d4 = map.get("fourth").get(0);
                KeyedDataPointGeneral d5 = map.get("fifth").get(0);
                KeyedDataPointGeneral d6 = map.get("sixth").get(0);
                collector.collect(new Tuple6<>(d1, d2, d3, d4, d5, d6));
            } else if (map.get("first").size() == 6) { // get result of iteration pattern
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("first").get(1);
                KeyedDataPointGeneral d3 = map.get("first").get(2);
                KeyedDataPointGeneral d4 = map.get("first").get(3);
                KeyedDataPointGeneral d5 = map.get("first").get(4);
                KeyedDataPointGeneral d6 = map.get("first").get(5);
                collector.collect(new Tuple6<>(d1, d2, d3, d4, d5, d6));
            }

        }
    }

    public static class GetResultTuple9 implements PatternFlatSelectFunction<KeyedDataPointGeneral, Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>> collector) throws Exception {
            if (map.get("first").size() == 1) { // get result of sequence pattern
                /**
                 * TODO
                 */
            } else if (map.get("first").size() == 9) { // get result of iteration pattern
                KeyedDataPointGeneral d1 = map.get("first").get(0);
                KeyedDataPointGeneral d2 = map.get("first").get(1);
                KeyedDataPointGeneral d3 = map.get("first").get(2);
                KeyedDataPointGeneral d4 = map.get("first").get(3);
                KeyedDataPointGeneral d5 = map.get("first").get(4);
                KeyedDataPointGeneral d6 = map.get("first").get(5);
                KeyedDataPointGeneral d7 = map.get("first").get(6);
                KeyedDataPointGeneral d8 = map.get("first").get(7);
                KeyedDataPointGeneral d9 = map.get("first").get(8);
                collector.collect(new Tuple9<>(d1, d2, d3, d4, d5, d6, d7, d8, d9));
            }

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

    public static class ExtractTimestampNOT2 implements AssignerWithPeriodicWatermarks<Tuple2<KeyedDataPointGeneral, Long>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestampNOT2() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestampNOT2(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple2<KeyedDataPointGeneral, Long> element, long l) {
            long timestamp = element.f0.getTimeStampMs();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestampKeyedDataPointGeneral2Int implements AssignerWithPeriodicWatermarks<Tuple3<KeyedDataPointGeneral, Integer, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestampKeyedDataPointGeneral2Int() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestampKeyedDataPointGeneral2Int(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple3<KeyedDataPointGeneral, Integer, Integer> element, long l) {
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

    public static class TimeComparatorKDG implements Comparator<KeyedDataPointGeneral> {
        @Override
        public int compare(KeyedDataPointGeneral t1, KeyedDataPointGeneral t2) {
            if (t1.getTimeStampMs() < t2.getTimeStampMs()) return -1;
            if (t1.getTimeStampMs() == t2.getTimeStampMs()) return 0;
            if (t1.getTimeStampMs() > t2.getTimeStampMs()) return 1;
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

    // TimeStampAssigners
    public static class ExtractTimestamp2KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp2KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp2KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f2;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp2KeyedDataPointGeneralLong implements AssignerWithPeriodicWatermarks<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp2KeyedDataPointGeneralLong() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp2KeyedDataPointGeneralLong(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> element, long l) {
            long timestamp = element.f2;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp3KeyedDataPointGeneralLong implements AssignerWithPeriodicWatermarks<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp3KeyedDataPointGeneralLong() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp3KeyedDataPointGeneralLong(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> element, long l) {
            long timestamp = element.f3;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp3KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp3KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp3KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f3;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class LatencyMapSeq implements MapFunction<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>, Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> {
        @Override
        public Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> map(Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral> dp) throws Exception {
            long latency = System.currentTimeMillis() - dp.f1.getCreationTime();
            return new Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>(dp.f0, dp.f1, latency);
        }
    }

    public static class LatencyMapIter3 implements MapFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>, Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>> {
        @Override
        public Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> map(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral> dp) throws Exception {
            long latency = System.currentTimeMillis() - dp.f2.getCreationTime();
            return new Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>(dp.f0, dp.f1, dp.f2, latency);
        }
    }

    public static class ExtractTimestamp4KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp4KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp4KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f4;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp5KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp5KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp5KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f5;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp6KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp6KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp6KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f6;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp7KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp7KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp7KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f7;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class ExtractTimestamp8KeyedDataPointGeneralLongInt implements AssignerWithPeriodicWatermarks<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp8KeyedDataPointGeneralLongInt() {
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp8KeyedDataPointGeneralLongInt(long periodMs) {
            this.maxOutOfOrderness = (periodMs);
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> element, long l) {
            long timestamp = element.f8;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    // KeySelectors

    public static class getArtificalKeyT4 implements KeySelector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f3;
        }
    }

    public static class getKeyT3 implements KeySelector<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>, String> {
        @Override
        public String getKey(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> data) throws Exception {
            return data.f0.getKey();
        }
    }

    public static class getKeyT4 implements KeySelector<Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral, Long>, String> {
        @Override
        public String getKey(Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> data) throws Exception {
            return data.f0.getKey();
        }
    }

    public static class getArtificalKeyT5 implements KeySelector<Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple5<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f4;
        }
    }

    public static class getArtificalKeyT6 implements KeySelector<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f5;
        }
    }

    public static class getArtificalKeyT7 implements KeySelector<Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple7<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f6;
        }
    }

    public static class getArtificalKeyT8 implements KeySelector<Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple8<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f7;
        }
    }

    public static class getArtificalKeyT9 implements KeySelector<Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple9<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f8;
        }
    }

    public static class getArtificalKeyT10 implements KeySelector<Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer>, Integer> {
        @Override
        public Integer getKey(Tuple10<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long, Integer> data) throws Exception {
            return data.f9;
        }
    }
}
