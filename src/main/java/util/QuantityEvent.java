package util;

import java.util.Date;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuantityEvent)) return false;
        QuantityEvent that = (QuantityEvent) o;
        return this.getKey().equals(that.getKey()) && this.getTimeStampMs() == that.getTimeStampMs();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey(), this.getTimeStampMs(), this.type);
    }

    @Override
    public String toString() {
        Date date = new Date(getTimeStampMs());
        return date + "," + getKey() + "," + getValue() + ", " + getType() + ", " + "POINT(" + getLongitude() + ", " + getLatitude() + ")" ;
    }
}