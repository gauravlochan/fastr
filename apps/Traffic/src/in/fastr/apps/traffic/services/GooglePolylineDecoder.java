package in.fastr.apps.traffic.services;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

// http://jeffreysambells.com/posts/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java/
public class GooglePolylineDecoder {

	public static List<GeoPoint> decodePoly(String encoded) {

	    List<GeoPoint> poly = new ArrayList<GeoPoint>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
	             (int) (((double) lng / 1E5) * 1E6));
	        poly.add(p);
	    }

	    return poly;
	}
}

