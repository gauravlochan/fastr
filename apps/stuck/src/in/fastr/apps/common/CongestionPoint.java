package in.fastr.apps.common;

/** Data class for each congestion point.
 * 
 * @author gaurav
 */
public class CongestionPoint {
    long epochTime;
    double latitude;
    double longitude;
    float speed;
    float accuracy;
    
    public CongestionPoint(double latitude, double longitude, long epochTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.epochTime = epochTime;
    }

    public CongestionPoint(long epochTime, double latitude, double longitude, 
            float speed, float accuracy) { 
        this.epochTime = epochTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public long getEpochTime() {
        return epochTime;
    }

    @Override
    public String toString() {
        return String.format("CongestionPoint: time=%d lat=%f long=%f", 
                epochTime, latitude, longitude);
    }

}
