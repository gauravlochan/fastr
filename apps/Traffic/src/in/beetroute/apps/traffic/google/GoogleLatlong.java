package in.beetroute.apps.traffic.google;

import in.beetroute.apps.commonlib.SimpleGeoPoint;

import com.google.gson.annotations.SerializedName;

public class GoogleLatlong {
    @SerializedName("lat")
    public Double latitude;

    @SerializedName("lng")
    public Double longitude;
    
    public SimpleGeoPoint getSimpleGeoPoint() {
    	return new SimpleGeoPoint(latitude, longitude);
    }
}
