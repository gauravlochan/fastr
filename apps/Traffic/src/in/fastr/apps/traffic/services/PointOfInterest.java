package in.fastr.apps.traffic.services;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

/**
 * A single point of interest
 * 
 * @author gauravlochan
 *
 */
public class PointOfInterest implements Serializable {
	public static final int LATLONG = 1;
	public static final int GOOGLE = 1;

	/**
	 * Identifies which service this Point of Interest came from. 
	 */
	private ServiceProviders service;

	/**
	 * Each service has their own identifier for a point of interest
	 */
	private String identifier;

	private double latitude;
	private double longitude;
	
	/**
	 * A name for this point
	 */
	private String name;
	
	/**
	 * A short description for this point
	 */
	private String description;
	
	
	public PointOfInterest(ServiceProviders sp) {
		service = sp;
	}
	
	public PointOfInterest(ServiceProviders sp, String identifier, String name, String description) {
		service = sp;
		this.identifier = identifier;
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public GeoPoint getGeoPoint() {
		return new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
	}
	
	public void setLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	

}
