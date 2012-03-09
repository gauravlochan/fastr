package in.beetroute.apps.traffic;


import in.beetroute.apps.commonlib.ServiceProviders;
import in.beetroute.apps.commonlib.SimpleGeoPoint;

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
	 * Some services have their own identifier for a point of interest.
	 * This may be useful if we ever need to refer back to this place in that service.
	 * e.g. Onze has an ID, Yahoo has WOEID
	 */
	private String identifier;

	/** 
	 * The Geo point (lat long) for this point
	 */
	private SimpleGeoPoint location;
	
	/**
	 * A name for this point
	 */
	private String name;
	
	/**
	 * A short description for this point
	 */
	private String description;

	/**
	 * 
	 * @param sp Service Provider who's returning 
	 * @param identifier
	 * @param name
	 * @param description
	 */
	public MapPoint(ServiceProviders sp, String name, String description) {
		service = sp;
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
		return location.getGeoPoint();
	}
	
	public SimpleGeoPoint getSimpleGeoPoint() {
	    return location;
	}
	
	/**
	 * Set the service specific identifier.
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
	    this.identifier = identifier;
	}
	
	public void setLocation(double latitude, double longitude) {
		this.location = new SimpleGeoPoint(latitude, longitude);
	}
	
}
