package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;
import in.fastr.apps.traffic.json.google.directions.DirectionsRoute;
import in.fastr.apps.traffic.json.google.directions.Result;
import in.fastr.library.RESTHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class GoogleDirectionsService implements DirectionsService {
	private static final String jsonServiceUrl = "http://maps.googleapis.com/maps/api/directions/json?";
	


	@Override
	public Route getFirstRoute(SimpleGeoPoint source, SimpleGeoPoint destination) {
		List<Route> routes = getRoutes(source, destination);
		return routes.get(0);
	}
	
	@Override
	public List<Route> getRoutes(SimpleGeoPoint source, SimpleGeoPoint destination) {
		String jsonResult = getRoutesJson(source, destination);
	
		Gson gson = new Gson();
		Result result = gson.fromJson(jsonResult, Result.class);
		List<DirectionsRoute> routes = result.routes;

		return RouteFromDirectionsRoutes(routes);
	}

	/**
	 * Call the REST service with the source and destination, and get back the JSON
	 * object
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	private String getRoutesJson(SimpleGeoPoint source, SimpleGeoPoint destination) {
		String request = appendSensor(jsonServiceUrl, true);
		request = appendSource(request, source);
		request = appendDestination(request, destination);
		
		String jsonResult = RESTHelper.simpleGet(request);
		return jsonResult;
	}

	
	// TODO: Dummy function to compile.  Need to implement this.
	private List<Route> RouteFromDirectionsRoutes(List<DirectionsRoute> dirRoutes) {
		List<Route> routes = new ArrayList<Route>();
		return routes;
	}
	
	
	// Simple function to append sensor to the requestUrl.  Should be the first append
	private static String appendSensor(String requestUrl, boolean sensor) {
		if (sensor) {
			return requestUrl.concat("sensor=true");
		} else {
			return requestUrl.concat("sensor=false");
		}
		
	}

	private static String appendSource(String requestUrl, SimpleGeoPoint source) {
		return (requestUrl.concat("&origin=")).concat(source.toString());
	}

	private static String appendDestination(String requestUrl, SimpleGeoPoint destination) {
		return (requestUrl.concat("&destination=")).concat(destination.toString());
	}



	
}
