/**
 * 
 */
package in.beetroute.apps.traffic.google.geocoding;

import in.beetroute.apps.commonlib.RESTHelper;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.services.GeocodingService;

import java.util.List;

import android.location.Address;

/**
 * http://code.google.com/apis/maps/documentation/geocoding/
 * 
 * @author gauravlochan
 *
 */
public class GoogleGeocodingService implements GeocodingService {

	//private static final String xmlServiceUrl = "http://maps.googleapis.com/maps/api/geocode/json?";
	private static final String jsonServiceUrl = "http://maps.googleapis.com/maps/api/geocode/xml?";

	
	/* (non-Javadoc)
	 * @see in.fastr.apps.traffic.services.GeocodingService#resolveAddress(java.lang.String)
	 */
	@Override
	public List<MapPoint> resolveAddress(String address) {
		String request = appendAddress(jsonServiceUrl, address);
		request = appendSensor(request, false);

		String result = RESTHelper.simpleGet(request);

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

    @Override
    public List<Address> resolveLocation(SimpleGeoPoint sgPoint, int maxResults) {
        // TODO Auto-generated method stub
        return null;
    }

}
