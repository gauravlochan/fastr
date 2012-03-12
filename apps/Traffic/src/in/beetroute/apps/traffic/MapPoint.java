package in.beetroute.apps.traffic;


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
	 * This map point accuracy
	 * 
     * Optional.
	 */
	private float accuracy;
	
    /**
     * 
     * @param name
     * @param description
     * @param identifier
     */
    public MapPoint(String name, String description, SimpleGeoPoint location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }


   /**
     * 
     * @param name
     * @param description
     * @param identifier
     */
    public MapPoint(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.location = new SimpleGeoPoint(latitude, longitude);
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
	
	public void setAccuracy(float accuracy) {
	    this.accuracy = accuracy;
	}
}
