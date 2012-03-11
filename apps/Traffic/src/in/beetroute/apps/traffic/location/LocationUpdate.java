package in.beetroute.apps.traffic.location;

import java.util.Date;

import android.location.Location;

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
    
    public LocationUpdate(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();
        epochTime = location.getTime();
    }
    
    public LocationUpdate(long epochTime, double latitude, double longitude, 
            float speed, float accuracy) { 
        this.epochTime = epochTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;
    }

    public float getAccuracy() {
        return accuracy;
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
        Date timeStamp = new Date(epochTime);
        return String.format("LocationUpdate: time=%s lat=%f long=%f speed=%f accuracy=%f", 
                timeStamp.toLocaleString(), latitude, longitude, speed, accuracy);
    }

}
