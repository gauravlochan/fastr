package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple data object representing the directions for a route
 * 
 * @author gauravlochan
 */
public class Route {
	/**
	 * Identifies which service these directions came from. 
	 */
	private ServiceProviders service;
	
	private SimpleGeoPoint source;
	private SimpleGeoPoint destination;
	
	// TODO: Constructor that specifies number of points
	private ArrayList<SimpleGeoPoint> points;
	
	public int drivingDistanceMeters;
	public int estimatedTimeSeconds;

	public Route() {
		points = new ArrayList<SimpleGeoPoint>();
	}
	
	// TODO: Think about how to store the distance and time estimates for each leg
	// TODO: Think about how to store the associated directions text
	public void addPoint(SimpleGeoPoint point) {
		points.add(point);
	}
	
	public List<SimpleGeoPoint> getPoints() {
		return this.points;
	}
}
