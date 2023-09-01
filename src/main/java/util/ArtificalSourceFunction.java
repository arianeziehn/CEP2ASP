package util;

import org.apache.flink.streaming.api.functions.source.SourceFunction;


import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.round;

public class ArtificalSourceFunction implements SourceFunction<KeyedDataPointGeneral> {
    private volatile boolean isRunning = true;
    public static final int RECORD_SIZE_IN_BYTE = 89;
    private String key;
    private String typeName;  // the source file
    private long throughput;
    boolean manipulateIngestionRate;
    private int windowsize;
    private long startTime;

    private int runtime;
    double quantileStart;
    double quantileEnd;
    int selectivity;
    String operatorType = "IRRELEVANT";
    public ArtificalSourceFunction(String typeName, long throughput, int windowsize, int runtime, double quantileStart, double quantileEnd, int selectivity) {
        this.typeName = typeName;
        this.key = null;
        this.runtime = runtime;
        this.windowsize = windowsize;
        this.throughput = throughput;
        this.selectivity = selectivity;
        this.quantileStart = quantileStart;
        this.quantileEnd = quantileEnd;
        if (throughput == 0) {
            this.manipulateIngestionRate = false;
        } else {
            this.manipulateIngestionRate = true;
        }
    }

    public ArtificalSourceFunction(String typeName, long throughput, int windowsize, int runtime, double quantileStart, double quantileEnd, int selectivity, String operatorType) {
        this.typeName = typeName;
        this.key = null;
        this.runtime = runtime;
        this.windowsize = windowsize;
        this.throughput = throughput;
        this.selectivity = selectivity;
        this.quantileStart = quantileStart;
        this.quantileEnd = quantileEnd;
        if (throughput == 0) {
            this.manipulateIngestionRate = false;
        } else {
            this.manipulateIngestionRate = true;
        }
        this.operatorType = operatorType;
    }

    public ArtificalSourceFunction(String typeName, long throughput, int windowsize, int runtime, int selectivity, String operatorType) {
        this.typeName = typeName;
        this.key = null;
        this.runtime = runtime;
        this.windowsize = windowsize;
        this.throughput = throughput;
        this.selectivity = selectivity;
        this.quantileStart = 0.0;
        this.quantileEnd = 1;
        if (throughput == 0) {
            this.manipulateIngestionRate = false;
        } else {
            this.manipulateIngestionRate = true;
        }
        this.operatorType = operatorType;

    }

    public void run(SourceContext<KeyedDataPointGeneral> sourceContext) throws Exception {
        long start = System.currentTimeMillis();
        this.startTime = start;
        boolean run = true;
        long tupleCounter = 0;
        long millisSinceEpoch = 0;
        while(run){
        // Create stream of KeyedDataPoints
            Random r = new Random();
            HashSet<Integer> tempSet = new HashSet<Integer>();
            int startQ = (int) round((windowsize + 1) * quantileStart);
            int endQ = (int) round((windowsize + 1) * quantileEnd);
            if (startQ <= 0.0){
                startQ = 1;
            }

            if(this.operatorType.equals("NEG")){
                for(int j = startQ; j<= endQ; j++){
                    tempSet.add(j);
                }
            }else {
                for (int j = 1; j <= (windowsize * selectivity / 100); j++) {

                    int randomNum = ThreadLocalRandom.current().nextInt(startQ, endQ);
                    tempSet.add(randomNum);
                }
            }

            if (this.key == null) {
                this.key = "1";
            }

            for (int i = 1; i <= this.windowsize; i++ ) {
                float longitude;
                float latitude;
                if (!tempSet.contains(i)) {
                    float min_long = 8.013631444394647f;
                    float max_long = 10.123635396448115f;
                    longitude = min_long + r.nextFloat() * (max_long - min_long);

                    float min_lat = 49.528089218684734f;
                    float max_lat = 51.444480109675695f;
                    latitude = min_lat + r.nextFloat() * (max_lat - min_lat);
                }else{
                    longitude = 8.013631444394647f;
                    latitude = 49.528089218684734f;
                }

                double value = 0;
                KeyedDataPointGeneral event = null;

                if (Objects.equals(this.typeName, "Velocity")) {
                    double min_vel;
                    double max_vel;
                    if (!tempSet.contains(i)) {
                        min_vel = 5.00;
                        max_vel = 175.00;
                    }else{
                        min_vel = 175.00;
                        max_vel = 200.00;
                    }
                    value = min_vel + r.nextFloat() * (max_vel - min_vel);
                    if (this.operatorType.equals("ITER") && tempSet.contains(i)){
                        value = 175 + i;
                    }
                    event = new VelocityEvent(this.key,
                            millisSinceEpoch, value, longitude, latitude);

                } else if (Objects.equals(this.typeName, "Quantity")) {
                    double min_qua;
                    double max_qua;
                    if (!tempSet.contains(i)) {
                        min_qua = 0.00;
                        max_qua = 80.00;
                    }else{
                        min_qua = 80.00;
                        max_qua = 150.00;
                    }
                    value = min_qua + r.nextFloat() * (max_qua - min_qua);
                    if (this.operatorType.equals("ITER") && tempSet.contains(i)){
                        value = 80 + i;
                    }
                    event = new QuantityEvent(this.key,
                            millisSinceEpoch, value, longitude, latitude);

                } else if (Objects.equals(this.typeName, "PM10")) {
                    double min_pm10;
                    double max_pm10;
                    if (!tempSet.contains(i)) {
                        min_pm10 = 0.00;
                        max_pm10 = 30.00;
                    }else{
                        min_pm10 = 30.00;
                        max_pm10 = 150.00;
                    }
                    value = min_pm10 + r.nextFloat() * (max_pm10 - min_pm10);
                    if (this.operatorType.equals("ITER") && tempSet.contains(i)){
                        value = 30 + i;
                    }
                    event = new PartMatter10Event(this.key,
                            millisSinceEpoch, value, longitude, latitude);

                } else if (Objects.equals(this.typeName, "PM2")) {
                    double min_pm2;
                    double max_pm2;
                    if (!tempSet.contains(i)) {
                        min_pm2 = 0.00;
                        max_pm2 = 25.00;
                    }else{
                        min_pm2 = 30.00;
                        max_pm2 = 150.00;
                    }
                    value = min_pm2 + r.nextFloat() * (max_pm2 - min_pm2);
                    if (this.operatorType.equals("ITER") && tempSet.contains(i)){
                        value = 30 + i;
                    }
                    event = new PartMatter10Event(this.key,
                            millisSinceEpoch, value, longitude, latitude);

                } else if (Objects.equals(this.typeName, "Temperature")) {
                    double min_t;
                    double max_t;
                    if (!tempSet.contains(i)) {
                        min_t = -10.00;
                        max_t = 25.00;
                    }else{
                        min_t = 26.00;
                        max_t = 36.00;
                    }
                    value = min_t + r.nextFloat() * (max_t - min_t);
                    if (this.operatorType.equals("ITER") && tempSet.contains(i)){
                        value = 26 + i;
                    }
                    event = new TemperatureEvent(this.key,
                            millisSinceEpoch, value, longitude, latitude);

                } else if (Objects.equals(this.typeName, "Humidity")) {
                    double min_h;
                    double max_h;
                    if (!tempSet.contains(i)) {
                        min_h = 40.00;
                        max_h = 60.00;
                    }else{
                        min_h = 60.00;
                        max_h = 80.00;
                    }
                    value = min_h + r.nextFloat() * (max_h - min_h);
                    if (this.operatorType.equals("ITER") && tempSet.contains(i)){
                        value = 60 + i;
                    }
                    event = new HumidityEvent(this.key,
                            millisSinceEpoch, value, longitude, latitude);

                }

                if (event != null) sourceContext.collect(event);
                millisSinceEpoch += 60000;
                tupleCounter++;
            }

                if(tupleCounter >= throughput && manipulateIngestionRate){
                    long now = System.currentTimeMillis();
                    if ( ((1000-(now-start)) > 0) && ((now-this.startTime) < this.runtime * 60000L)) {
                        Thread.sleep(1000 - (now - start));
                    } else if ((now-this.startTime) >= this.runtime * 60000L) {
                        run = false;
                    }
                    tupleCounter = 0;
                    start = System.currentTimeMillis();
                }
            }
    }


    public void cancel() {
        this.isRunning = false;
    }
}
