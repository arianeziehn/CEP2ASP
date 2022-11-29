package util;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class UDFs {
    public static class ExtractTimestamp implements AssignerWithPeriodicWatermarks<KeyedDataPointGeneral> {
        private static final long serialVersionUID = 1L;
        private long maxOutOfOrderness;

        private long currentMaxTimestamp;

        public ExtractTimestamp(){
            this.maxOutOfOrderness = 0;
        }

        public ExtractTimestamp(long periodMs){
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

    public static class GetResultTuple implements PatternFlatSelectFunction<KeyedDataPointGeneral, String> {
        @Override
        public void flatSelect(Map<String, List<KeyedDataPointGeneral>> map, Collector<String> collector) throws Exception {
            collector.collect(map.toString());

        }
    }

    public static class DataKeySelector implements KeySelector<KeyedDataPointGeneral, String> {
        @Override
        public String getKey(KeyedDataPointGeneral keyedDataPoint) throws Exception {
            return keyedDataPoint.getKey();
        }
    }
}
