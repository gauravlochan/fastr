package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

/**
 * A single point of interest
 * 
 * @author gauravlochan
 *
 */
public class MapPoint implements Serializable {
	/**
	 * Identifies which service this Point of Interest came from. 
	 */
	private ServiceProviders service;

	/**
	 * Each service has their own identifier for a point of interest
	 */
	private String identifier;

	/** 
	 * The Geo point (lat long) for this point
	 */
	private SimpleGeoPoint sgPoint;
	
	/**
	 * A name for this point
	 */
	private String name;
	
	/**
	 * A short description for this point
	 */
	private String description;
	
	
	public MapPoint(ServiceProviders sp) {
		service = sp;
	}
	
	public MapPoint(ServiceProviders sp, String identifier, String name, String description) {
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
		return sgPoint.getGeoPoint();
	}
	
	public void setLocation(double latitude, double longitude) {
		this.sgPoint = new SimpleGeoPoint(latitude, longitude);
	}
	

}
