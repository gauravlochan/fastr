package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;

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
	private ServiceProviders service;
	
	public SimpleGeoPoint source;
	public SimpleGeoPoint destination;
	
	// Even though GeoPoint would have been better to store, it's not serializable
	// and so gson couldn't convert it to json.  (ummm, i think)
	private ArrayList<SimpleGeoPoint> points;
	
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
	
	public List<SimpleGeoPoint> getPoints() {
		return this.points;
	}
}
