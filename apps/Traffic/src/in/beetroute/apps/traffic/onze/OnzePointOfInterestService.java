package in.beetroute.apps.traffic.onze;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.RESTHelper;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.services.PointOfInterestService;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnzePointOfInterestService implements PointOfInterestService {
    private static final String TAG = Global.COMPANY;

	private static final String api_key = "pihack";
	private static final String jsonServiceUrl = "http://latlong.in/api/v1/search?api_key="+ api_key;
	
	// Keys from the json object returned by the API
	private static final String name_key = "name";
	private static final String phone_key = "ph";
	private static final String id_key = "id";
	private static final String point_key = "point";
	private static final String ls_key = "LS";
	
	/**
	 * Gets a list of point of interests from the latlong service
	 */
	@Override
	public List<MapPoint> getPoints(String nameOfPlace) {
		String request = appendName(jsonServiceUrl, nameOfPlace);
		String result = RESTHelper.simpleGet(request);
		
		ArrayList<MapPoint> list = new ArrayList<MapPoint>();
		if (result == null) { 
		    return list;
		}
		
		
        JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			Logger.info(TAG, "Number of entries " + jsonArray.length());
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				MapPoint point = getPointOfInterest(jsonObject);
				if (point != null) {
				    list.add(point);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return list;
	}
        
    
	/**
	 * Convert a point of interest json object into our java PointOfInterest
	 * 
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
    private MapPoint getPointOfInterest(JSONObject jsonObject) throws JSONException {
		String name = jsonObject.getString(name_key);
		String desc = extractDescription(jsonObject.getString(ls_key));

        // Now to extract the latlong
        String coordinates = jsonObject.getString(point_key);
        int comma = coordinates.indexOf(',');
        if (comma == -1) {
            Logger.warn(TAG, "getPOI didn't have coordinates for "+name);
            return null;
        }
        String longitude = coordinates.substring(0, comma);
        String latitude = coordinates.substring(comma+1);
        SimpleGeoPoint location = new SimpleGeoPoint(latitude, longitude);

        // Get a MapPoint from this data
		MapPoint point = new MapPoint(name, desc, location);

		return point;
    }
    
	private static String appendName(String requestUrl, String name) {
		return (requestUrl.concat("&query=")).concat(name.replace(' ', '+'));
	}
	
	/** 
	 * Remove the <br/> tags from the description returned by latlong API
	 * @param rawDescription
	 * @return
	 */
	private String extractDescription(String rawDescription) {
	    return rawDescription.replace("<br/>", "");
	    
	}
	
}
