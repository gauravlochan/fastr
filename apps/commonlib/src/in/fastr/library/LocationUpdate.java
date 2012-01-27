package in.fastr.library;

/** 
 * Data class for each location update that is to be sent to the server
 * 
 * @author gaurav
 */
public class LocationUpdate {
    long epochTime;
    double latitude;
    double longitude;
    float speed;
    float accuracy;
    
    public LocationUpdate(double latitude, double longitude, long epochTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.epochTime = epochTime;
    }

    public LocationUpdate(long epochTime, double latitude, double longitude, 
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
