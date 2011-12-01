/**
 * 
 */
package in.fastr.apps.traffic.services;

import in.fastr.library.RESTHelper;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;

/**
 * http://code.google.com/apis/maps/documentation/geocoding/
 * 
 * @author gauravlochan
 *
 */
public class GoogleGeocodingService implements GeocodingService {

	private static final String xmlServiceUrl = "http://maps.googleapis.com/maps/api/geocode/json?";
	private static final String jsonServiceUrl = "http://maps.googleapis.com/maps/api/geocode/xml?";

	
	/* (non-Javadoc)
	 * @see in.fastr.apps.traffic.services.GeocodingService#resolveAddress(java.lang.String)
	 */
	@Override
	public GeoPoint resolveAddress(String address) {
		String request = appendAddress(jsonServiceUrl, address);
		request = appendSensor(request, false);

		String result = RESTHelper.simpleGet(request);

		Geocoder g;
		
		// TODO convert response json to GeoPoint
		return null;
	}
	
	private static String appendAddress(String requestUrl, String address) {
		return (requestUrl.concat("address=")).concat(address.replace(' ', '+'));
	}
	
	// Simple function to append sensor to the requestUrl
	private static String appendSensor(String requestUrl, boolean sensor) {
		if (sensor) {
			return requestUrl.concat("&sensor=true");
		} else {
			return requestUrl.concat("&sensor=false");
		}
		
	}


}
