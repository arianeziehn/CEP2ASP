package util;

import java.util.Date;

public class QuantityEvent extends KeyedDataPointGeneral {

    private String type;

    public QuantityEvent() {
        super();
        this.type = "quantity";
    }

    public QuantityEvent(String key, long timeStampMs, Double value) {
        super(key, timeStampMs, value);
        this.type = "quantity";
    }

    public QuantityEvent(String key, long timeStampMs, Double value, float longitude, float latitude) {
        super(key, longitude, latitude, timeStampMs, value);
        this.type = "quantity";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        Date date = new Date(getTimeStampMs());
        return date + "," + getKey() + "," + getValue() + ", " + getType() + ", " + "POINT(" + getLongitude() + ", " + getLatitude() + ")" ;
    }
}