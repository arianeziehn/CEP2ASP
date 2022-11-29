/**
 * (c) dataartisans
 * https://data-artisans.com/blog/robust-stream-processing-flink-walkthrough
 *
 */

package util;
import java.util.Date;

/**
 * Presentation of a data point for CEP2ASP with additional geographial information and sensor identifier (key)
 * @param <Double>
 */
public class KeyedDataPointGeneral<Double> extends DataPoint<Double> {

    private String key;
    private float longitude;
    private float latitude;

    public KeyedDataPointGeneral(){
        super();
        this.key = null;
        this.latitude = 0.0f;
        this.longitude = 0.0f;
    }

    public KeyedDataPointGeneral(String key, long timeStampMs, Double value) {
        super(timeStampMs, value);
        this.key = key;
        this.latitude = 0.0f;
        this.longitude = 0.0f;
    }

    public KeyedDataPointGeneral(String key, long timeStampMs, String type, Double value) {
        super(timeStampMs, value, type);
        this.key = key;
        this.latitude = 0.0f;
        this.longitude = 0.0f;
    }


    public KeyedDataPointGeneral(String key, float longitude, float latitude, long timeStampMs, Double value) {
        super(timeStampMs, value);
        this.key = key;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        Date date = new Date(getTimeStampMs());
        return date + "," + getKey() + "," + getValue() + ", POINT(" + getLongitude() + ", " + getLatitude() + ")" ;
    }
}
