package util;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.flink.api.java.tuple.Tuple3;

public class LatencyLogger extends RichFlatMapFunction<Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral,KeyedDataPointGeneral>, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyLogger.class);
    public LatencyLogger() {
    }

    @Override
    public void flatMap(Tuple3<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral> dp, Collector<Integer> collector) throws Exception {
        long latency = System.currentTimeMillis() - dp.f2.getCreationTime();
        Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long> message = new Tuple4<KeyedDataPointGeneral, KeyedDataPointGeneral, KeyedDataPointGeneral, Long>(dp.f0,dp.f1,dp.f2,latency);
        Log.info(message.toString());
    }
}
