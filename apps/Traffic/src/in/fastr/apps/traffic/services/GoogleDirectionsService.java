package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.SimpleGeoPoint;
import in.fastr.library.Global;
import in.fastr.library.RESTHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GoogleDirectionsService implements DirectionsService {
	private static final String jsonServiceUrl = "http://maps.googleapis.com/maps/api/directions/json?";
	
	private static final String routes_key = "routes";
	private static final String legs_key = "legs";

	private static final String steps_key = "steps";
	private static final String destination_key = "end_location";
	private static final String source_key = "start_location";
	private static final String lat_key = "lat";
	private static final String long_key = "lng";
	private static final String distance_key = "distance";
	private static final String duration_key = "duration";


	@Override
	public Route getRoute(SimpleGeoPoint source, SimpleGeoPoint destination) {
		
		String request = appendSensor(jsonServiceUrl, true);
		request = appendSource(request, source);
		request = appendDestination(request, destination);
		
		String result = RESTHelper.simpleGet(request);
		
		return getFirstRoute(result);
	}
	
	
	private Route getFirstRoute(String result) {
		Route route = new Route();

		try {

            JSONObject resultObject = new JSONObject(result);
		    JSONArray routes = resultObject.getJSONArray(routes_key);
		    Log.d(Global.Company, "number of routes = "+routes.length());
		    
		    JSONObject routeObject = routes.getJSONObject(0);
		    JSONArray legs = routeObject.getJSONArray(legs_key);
		    Log.d(Global.Company, "number of legs = "+legs.length());
		    
			for (int i = 0; i < legs.length(); i++) {
				JSONObject leg = legs.getJSONObject(i);
				addLegToRoute(leg, route, (i==legs.length()-1) );
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return route;
	}

	private void addLegToRoute(JSONObject leg, Route route, boolean is_last_leg) throws JSONException {
		JSONObject distance = leg.getJSONObject(distance_key);
		route.drivingDistanceMeters = distance.getInt("value");

		JSONObject duration = leg.getJSONObject(duration_key);
		route.estimatedTimeSeconds = duration.getInt("value");
		
		JSONArray steps = leg.getJSONArray(steps_key);
		for (int i = 0; i < steps.length(); i++) {
			JSONObject step = steps.getJSONObject(i);
			addStepToRoute(step, route, (is_last_leg&(i==steps.length()-1)) );
		}
	}
	
	private void addStepToRoute(JSONObject step, Route route, boolean is_last_leg_step) throws JSONException {
		JSONObject source = step.getJSONObject(source_key);
		double lat = source.getDouble(lat_key);
		double longi = source.getDouble(long_key);
		
		route.addPoint(new SimpleGeoPoint(lat, longi));
		
		// Since this is the last step of the last leg, remember to add the final geopoint
		if (is_last_leg_step) {
			JSONObject destination = step.getJSONObject(destination_key);
			lat = destination.getDouble(lat_key);
			longi = destination.getDouble(long_key);
			
			route.addPoint(new SimpleGeoPoint(lat, longi));
		}
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
