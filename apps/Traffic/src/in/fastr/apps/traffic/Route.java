package in.fastr.apps.traffic;


import in.fastr.library.ServiceProviders;
import in.fastr.library.SimpleGeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple data object representing the directions for a route
 * 
 * @author gauravlochan
 */
// TODO: Think about how to store the distance and time estimates for each leg
// TODO: Think about how to store the associated directions text
public class Route {
	/**
	 * Identifies which service these directions came from. 
	 */
	protected ServiceProviders service;
	
	public SimpleGeoPoint source;
	public SimpleGeoPoint destination;
	
	// Even though GeoPoint would have been better to store, it's not serializable
	// and so gson couldn't convert it to json.  (ummm, i think)
	protected ArrayList<SimpleGeoPoint> points;
	
	public int drivingDistanceMeters = 0;
	public int estimatedTimeSeconds = 0;

	public Route(ServiceProviders service) {
		this.service = service;
		points = new ArrayList<SimpleGeoPoint>();
	}
	
	public Route(ServiceProviders service, int numPoints) {
		this.service = service;
		points = new ArrayList<SimpleGeoPoint>(numPoints);
	}
	
	public void addPoint(SimpleGeoPoint point) {
		points.add(point);
	}
	
	/**
	 * Returns the list of points in this route. 
	 * 
	 * Modifying the list will modify the route, so be careful.
	 * @return
	 */
	public List<SimpleGeoPoint> getPoints() {
		return this.points;
	}
}
