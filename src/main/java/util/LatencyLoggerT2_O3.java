package util;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class logs the latency as average of all result tuples (Tuple2 of KeyedDataPoints) received within a second
 */
public class LatencyLoggerT2_O3 extends RichFlatMapFunction<Tuple2<KeyedDataPointGeneral, Integer>, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyLoggerT2_O3.class);
    private long totalLatencySum = 0;
    private long matchedPatternsCount = 0;
    private long lastLogTimeMs = -1;

    public LatencyLoggerT2_O3() {
    }

    @Override
    public void flatMap(Tuple2<KeyedDataPointGeneral, Integer> dp, Collector<Integer> collector) throws Exception {
        KeyedDataPointGeneral dp_last = dp.f0;
        log_latency(dp_last);
    }

    public void log_latency(KeyedDataPointGeneral last) {
        long currentTime = System.currentTimeMillis();
        long detectionLatency = currentTime - last.getCreationTime();

        this.totalLatencySum += detectionLatency;
        this.matchedPatternsCount += 1;

        if (lastLogTimeMs == -1) { //init
            lastLogTimeMs = currentTime;
            LOG.info("Starting Latency Logging for matched patterns with frequency 1 second.");
        }

        long timeDiff = currentTime - lastLogTimeMs;
        if (timeDiff >= 1000) {
            double eventDetectionLatencyAVG = this.totalLatencySum / this.matchedPatternsCount;
            String message = "LatencyLogger: $ On Worker: During the last $" + timeDiff + "$ ms, AVGEventDetLatSum: $" + eventDetectionLatencyAVG + "$, derived from a LatencySum: $" + totalLatencySum +
                    "$, and a matche Count of : $" + matchedPatternsCount + "$";
            Log.info(message);
            lastLogTimeMs = currentTime;
            totalLatencySum = 0;
            matchedPatternsCount = 0;
        }
    }

}
