package util;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThroughputLogger<T> extends RichFlatMapFunction<T, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(ThroughputLogger.class);

    private long totalReceived = 0;
    private long lastTotalReceived = 0;
    private long lastLogTimeMs = -1;
    private int elementSize;
    private long logfreq;
    private long query = 0;

    public ThroughputLogger(int elementSize, long logfreq) {
        this.elementSize = elementSize;
        this.logfreq = logfreq;
    }

    public ThroughputLogger(int elementSize, long logfreq, long query) {
        this.elementSize = elementSize;
        this.logfreq = logfreq;
        this.query = query;
    }

    @Override
    public void flatMap(T element, Collector<Integer> collector) throws Exception {
        totalReceived++;
        if (logfreq > 0 && totalReceived % logfreq == 0) {
            // throughput over entire time
            long now = System.currentTimeMillis();

            // throughput for the last "logfreq" elements
            if (lastLogTimeMs == -1) {
                // init (the first)
                lastLogTimeMs = now;
                lastTotalReceived = totalReceived;
            } else {
                long timeDiff = now - lastLogTimeMs;
                long elementDiff = totalReceived - lastTotalReceived;
                double ex = (1000 / (double) timeDiff);
                String message = "Worker: $" + getRuntimeContext().getIndexOfThisSubtask() + "$: During the last $" + timeDiff + "$ ms, we received $" + elementDiff + "$ elements. That's $" + (elementDiff * ex) + "$ elements/second/core and $" + (elementDiff * ex * elementSize / 1024 / 1024) + "$ MB/sec/core. GB received $" + ((totalReceived * elementSize) / 1024 / 1024 / 1024) + "$ Query $" + query + "$";
                //LOG.info(message);
                // in case of problems also try:
                Log.info(message);
                lastLogTimeMs = now;
                lastTotalReceived = totalReceived;
            }
        }
    }
}
