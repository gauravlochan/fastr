package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;

import java.util.List;

public interface DirectionsService {
	public Route getFirstRoute(SimpleGeoPoint source, SimpleGeoPoint destination);
	public List<Route> getRoutes(SimpleGeoPoint source, SimpleGeoPoint destination);

}
