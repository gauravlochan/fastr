package in.fastr.apps.traffic.onze;

import in.fastr.apps.traffic.MapPoint;
import in.fastr.apps.traffic.ServiceProviders;
import in.fastr.apps.traffic.services.PointOfInterestService;
import in.fastr.library.Global;
import in.fastr.library.Logger;
import in.fastr.library.MyLogger;
import in.fastr.library.RESTHelper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnzePointOfInterestService implements PointOfInterestService {
    private static Logger logger = new MyLogger(Global.Company);

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
		
        JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			logger.info("Number of entries " + jsonArray.length());

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				MapPoint point = getPointOfInterest(jsonObject);
				list.add(point);
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
		String id = jsonObject.getString(id_key);
		String desc = jsonObject.getString(ls_key);

		MapPoint point = new MapPoint(ServiceProviders.ONZE,
				id, name, desc);

		// Now to extract the latlong
		String coordinates = jsonObject.getString(point_key);
		int comma = coordinates.indexOf(',');
		String longitude = coordinates.substring(0, comma);
		String latitude = coordinates.substring(comma+1);
		
		point.setLocation(new Double(latitude), new Double(longitude));

		return point;
    }
    
	private static String appendName(String requestUrl, String name) {
		return (requestUrl.concat("&query=")).concat(name.replace(' ', '+'));
	}
	
}
