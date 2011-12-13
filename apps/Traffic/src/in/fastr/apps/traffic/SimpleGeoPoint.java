package in.fastr.apps.traffic;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

/**
 * A simple representation of a geopoint.
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
	
}
