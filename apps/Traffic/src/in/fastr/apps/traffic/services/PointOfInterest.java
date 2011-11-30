package in.fastr.apps.traffic.services;

import com.google.android.maps.GeoPoint;

/**
 * A single point of interest
 * 
 * @author gauravlochan
 *
 */
public class PointOfInterest {
	/**
	 * Identifies which service this Point of Interest came from. 
	 * TODO: Make this an enum
	 */
	private int Service;

	/**
	 * Each service has their own identifier for a point of interest
	 */
	private String identifier;

	/**
	 * The coordinates for this point of interest
	 */
	private GeoPoint geoPoint;
	
	/**
	 * A name for this Point of Interest
	 */
	private String name;

}
