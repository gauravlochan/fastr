package in.beetroute.apps.traffic.google.directions;

import in.beetroute.apps.commonlib.SimpleGeoPoint;

import java.util.ArrayList;
import java.util.List;

// http://jeffreysambells.com/posts/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java/
class PolylineDecoder {

	public static List<SimpleGeoPoint> decodePoly(String encoded) {

	    List<SimpleGeoPoint> poly = new ArrayList<SimpleGeoPoint>();
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
	        
	        SimpleGeoPoint sgPoint = new SimpleGeoPoint(
	                ((double) lat / 1E5),
	                ((double) lng / 1E5));
	        poly.add(sgPoint);

	    }

	    return poly;
	}
}

