package in.beetroute.apps.traffic.services;

import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.Route;

import java.util.List;

public interface DirectionsService {
	public Route getFirstRoute(SimpleGeoPoint source, SimpleGeoPoint destination);
	public List<Route> getRoutes(SimpleGeoPoint source, SimpleGeoPoint destination);

}
