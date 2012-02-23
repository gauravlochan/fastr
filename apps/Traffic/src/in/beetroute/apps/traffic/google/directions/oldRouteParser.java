package in.beetroute.apps.traffic.google.directions;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.ServiceProviders;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class uses the old JSON parsing, before it was replaced by Gson parsing
 * @author gauravlochan
 *
 */
@Deprecated
public class oldRouteParser {
    private static final String TAG = Global.COMPANY;

	private static final String routes_key = "routes";
	private static final String legs_key = "legs";

	private static final String steps_key = "steps";
	private static final String destination_key = "end_location";
	private static final String source_key = "start_location";
	private static final String lat_key = "lat";
	private static final String long_key = "lng";
	private static final String distance_key = "distance";
	private static final String duration_key = "duration";
	
	
	public Route getFirstRoute(String jsonResult) {
		Route route = new Route(ServiceProviders.GOOGLE);

		try {

            JSONObject resultObject = new JSONObject(jsonResult);
		    JSONArray routes = resultObject.getJSONArray(routes_key);
		    Log.d(TAG, "number of routes = "+routes.length());
		    
		    JSONObject routeObject = routes.getJSONObject(0);
		    JSONArray legs = routeObject.getJSONArray(legs_key);
		    Log.d(TAG, "number of legs = "+legs.length());
		    
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
		route.drivingDistanceMeters += distance.getInt("value");

		JSONObject duration = leg.getJSONObject(duration_key);
		route.estimatedTimeSeconds += duration.getInt("value");
		
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


}
