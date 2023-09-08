package util;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class logs the latency as average of all result tuples (Tuple6 of KeyedDataPoints) received within a second
 */

public class LatencyLoggerT6 extends RichFlatMapFunction<Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral>, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyLoggerT6.class);
    private long totalLatencySum = 0;
    private long matchedPatternsCount = 0;
    private long lastLogTimeMs = -1;
    private boolean logPerTuple = false; //enables logging per tuple

    public LatencyLoggerT6() {
    }

    public LatencyLoggerT6(boolean logPerTuple) {
        this.logPerTuple = logPerTuple;
    }

    @Override
    public void flatMap(Tuple6<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral> dp, Collector<Integer> collector) throws Exception {
        KeyedDataPointGeneral dp_last = dp.f5;
        log_latency(dp_last);
    }

    public void log_latency(KeyedDataPointGeneral last) {
        long currentTime = System.currentTimeMillis();
        long detectionLatency = currentTime - last.getCreationTime(); //named ingestion-time in report

        this.totalLatencySum += detectionLatency;
        this.matchedPatternsCount += 1;

        if (lastLogTimeMs == -1) { //init
            lastLogTimeMs = currentTime;
            LOG.info("Starting Latency Logging for matched patterns with frequency 1 second.");
        }

        long timeDiff = currentTime - lastLogTimeMs;
        if (timeDiff >= 1000 || this.logPerTuple) {
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
