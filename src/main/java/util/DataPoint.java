/**
 * (c) dataartisans
 * https://data-artisans.com/blog/robust-stream-processing-flink-walkthrough
 *
 */
package util;

/**
 * Simple presentation of a data point
 * @param <T> the type of value
 */
public class DataPoint<T> {

  private long timeStampMs;
  private T value;

  public DataPoint() {
    this.timeStampMs = 0;
    this.value = null;
  }

  public DataPoint(long timeStampMs, T value) {
    this.timeStampMs = timeStampMs;
    this.value = value;
  }

  public DataPoint(long timeStampMs, T value, String type) {
    this.timeStampMs = timeStampMs;
    this.value = value;
  }

  public long getTimeStampMs() {
    return timeStampMs;
  }

  public void setTimeStampMs(long timeStampMs) {
    this.timeStampMs = timeStampMs;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "DataPoint(timestamp=" + timeStampMs + ", value=" + value + ")";
  }
}
