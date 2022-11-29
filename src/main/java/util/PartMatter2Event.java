package util;

import java.util.Date;

public class PartMatter2Event extends KeyedDataPointGeneral{

    private String type;

    public PartMatter2Event(){
        super();
        this.type = "PM2.5";
    }

    public PartMatter2Event(String key, long timeStampMs, Double value) {
        super(key, timeStampMs, value);
        this.type = "PM2.5";
    }

    public PartMatter2Event(String key, long timeStampMs, Double value, float longitude, float latitude) {
        super(key, longitude, latitude, timeStampMs, value);
        this.type = "PM2.5";
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
