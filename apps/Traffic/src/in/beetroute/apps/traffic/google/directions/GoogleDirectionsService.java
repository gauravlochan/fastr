package in.beetroute.apps.traffic.google.directions;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.RESTHelper;
import in.beetroute.apps.commonlib.ServiceProviders;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.services.DirectionsService;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class GoogleDirectionsService implements DirectionsService {
    private static final String TAG = Global.COMPANY;
	private static final String jsonServiceUrl = "http://maps.googleapis.com/maps/api/directions/json?";

	@Override
	public Route getFirstRoute(SimpleGeoPoint source, SimpleGeoPoint destination) {
	    // TODO: Optimize this to only get a single route from the API
		List<Route> routes = getRoutes(source, destination);
		if (routes.size() > 0) {
		    return routes.get(0);
		} else {
		    return null;
		}
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
	    float THRESHOLD = 0.010f; // 10 meters
	    
		// This is where we can choose what points we want to add
		// For now i'm simply going to add the points from the polyline
		String points = step.polyline.points;
		
		List<SimpleGeoPoint> simpleGeoPoints = PolylineDecoder.decodePoly(points);
		SimpleGeoPoint previous = null;
		
		for (SimpleGeoPoint sgPoint: simpleGeoPoints) {
		    if (previous == null) {
		        route.addPoint(sgPoint);
		        previous = sgPoint;
		    } else {
	            // Only keep points that are not too close.  It slows down the map
	            // rendering on the phone.
    		    if (previous.getDistanceFrom(sgPoint) > THRESHOLD) {
    		        route.addPoint(sgPoint);
    		        // Note: Only update previous to a point that was added to the route
    		        previous = sgPoint;
    		    } else {
    		          //Logger.debug(TAG, "Dropping point " + sgPoint.toString());
    		    }
		    }
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
		// TODO: Config variable for alternative routes
		request = appendAlternativeRoutes(request, true);
		
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
	
    private static String appendAlternativeRoutes(String requestUrl, boolean alternatives) {
        if (alternatives) {
            return requestUrl.concat("&alternatives=true");
        } else {
            return requestUrl.concat("&alternatives=false");
        }
        
    }
	
}
