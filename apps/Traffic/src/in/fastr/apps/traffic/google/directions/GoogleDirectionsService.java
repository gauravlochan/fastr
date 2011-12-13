package in.fastr.apps.traffic.google.directions;

import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.ServiceProviders;
import in.fastr.apps.traffic.SimpleGeoPoint;
import in.fastr.apps.traffic.services.DirectionsService;
import in.fastr.library.RESTHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
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
		List<DirectionsRoute> dirRoutes = result.routes;
		
		// Convert Googles DirectionsRoutes to our Routes
		List<Route> routes = new ArrayList<Route>();
		for (DirectionsRoute dr: dirRoutes) {
			// Create a route object for each directionRoute
			Route route = new Route(ServiceProviders.GOOGLE);
			route.source = source;
			route.destination = destination;
			
			// Add the legs to the route object
			for (int i=0; i< dr.legs.size(); i++) {
				Leg leg = dr.legs.get(i);
				addLegToRoute(route, leg, (i== dr.legs.size()-1));
			}
			routes.add(route);
		}
		return routes;
	}
	
	private void addLegToRoute(Route route, Leg leg, boolean isLastLeg) {
		route.drivingDistanceMeters += leg.distance.value;
		route.estimatedTimeSeconds += leg.duration.value;
		
		for (int i=0; i < leg.steps.size(); i++) {
			Step step = leg.steps.get(i);
			addStepToRoute(route, step, isLastLeg & (i==leg.steps.size() -1));
		}
	}
	
	private void addStepToRoute(Route route, Step step, boolean isLastStep) {
		// This is where we can choose what points we want to add
		// For now i'm simply going to add the points from the polyline
		String points = step.polyline.points;
		
		List<GeoPoint> geoPoints = PolylineDecoder.decodePoly(points);
		for (GeoPoint geoPoint: geoPoints) {
			SimpleGeoPoint sgPoint = new SimpleGeoPoint(geoPoint);
			route.addPoint(sgPoint);
		}
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
