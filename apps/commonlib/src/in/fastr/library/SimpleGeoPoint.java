package in.fastr.library;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

/**
 * A simple representation of a geopoint.
 * 
 * Why did i introduce a class other than GeoPoint? I think it had
 * something to do with GeoPoint not being serializable so I
 * couldn't pass it between intents
 * 
 * @author gauravlochan
 *
 */
public class SimpleGeoPoint implements Serializable {
	private static final long serialVersionUID = 1L;

	private double latitude;
	private double longitude;
	
	public SimpleGeoPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	// constructor that takes in GeoPoint
	public SimpleGeoPoint(GeoPoint geoPoint) {
		latitude = geoPoint.getLatitudeE6() / 1000000F;
		longitude = geoPoint.getLongitudeE6() / 1000000F;
	}

	public GeoPoint getGeoPoint() {
		return new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
	}
	
	@Override
	public String toString() {
		return latitude+","+longitude;
	}
	
	public double getLatitude() {
	    return latitude;
	}
	
	public double getLongitude() {
	    return longitude;
	}
	
	/**
	 * Returns distance from the other point
	 * 
	 * @param point2
	 * @return distance in km
	 */
	public double getDistanceFrom(SimpleGeoPoint point2) {
        return getDistance(getLatitude(), getLongitude(), 
                point2.getLatitude(), point2.getLongitude() );
    }
    
    // from http://stackoverflow.com/questions/8494283/gps-distance-calculation
    // haversines formula
    public static double getDistance(double lat1, double lng1, double lat2, double lng2){
        double R = 6371; // earth’s radius (mean radius = 6,371km)
        double dLat =  Math.toRadians(lat2-lat1);

        double dLon =  Math.toRadians(lng2-lng1); 
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
                   Math.sin(dLon/2) * Math.sin(dLon/2); 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        double dr1 = R * c; //in radians      

        return dr1;
    }
	
}
