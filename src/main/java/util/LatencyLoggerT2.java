package util;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatencyLoggerT2 extends RichFlatMapFunction<Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral>, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyLoggerT2.class);

    public LatencyLoggerT2() {
    }

    @Override
    public void flatMap(Tuple2<KeyedDataPointGeneral, KeyedDataPointGeneral> dp, Collector<Integer> collector) throws Exception {
        long latency = System.currentTimeMillis() - dp.f1.getCreationTime();
        Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long> message = new Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, Long>(dp.f0,dp.f1,latency);
        Log.info(message.toString());
    }
}
