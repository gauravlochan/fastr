package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;

public interface DirectionsService {
	public Route getRoute(SimpleGeoPoint source, SimpleGeoPoint destination);

	// TODO: public ArrayList<Route> getRoutes(SimpleGeoPoint source, SimpleGeoPoint destination);
	
	// TODO: Interfaces for getting routes for source/destination in different formats

}
