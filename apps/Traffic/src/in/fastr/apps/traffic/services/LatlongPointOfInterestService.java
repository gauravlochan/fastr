package in.fastr.apps.traffic.services;

import in.fastr.library.rest.RESTHelper;

import java.util.List;

public class LatlongPointOfInterestService implements PointOfInterestService {
	private static final String api_key = "pihack";
	private static final String jsonServiceUrl = "http://latlong.in/api/v1/search?api_key="+ api_key;

	@Override
	public List<PointOfInterest> getPoints(String nameOfPlace) {
		String request = appendName(jsonServiceUrl, nameOfPlace);
		String result = RESTHelper.simpleGet(request);
		
        // TODO convert response json to GeoPoint
		return null;
	}
	
	private static String appendName(String requestUrl, String name) {
		return (requestUrl.concat("&query=")).concat(name.replace(' ', '+'));
	}
	
}
