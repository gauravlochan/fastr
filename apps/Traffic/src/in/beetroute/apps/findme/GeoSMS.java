package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.google.geocoding.AndroidGeocodingService;
import in.beetroute.apps.traffic.services.GeocodingService;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Location;

// http://en.wikipedia.org/wiki/Geo_URI
// e.g. geo:37.786971,-122.399677;u=35 (where u is uncertainty in meters)
public class GeoSMS {
    private static final String TAG = Global.COMPANY;
    
    // IMPORTANT: If you change these messages, you risk breaking things for users 
    // on an older version of BeetRoute.  Think hard about the impact of the change.
    // If you do change the message, check to see if extractLocation should be changed
    private static final String TEMPLATE_WITH_LOCATION = 
            "I am near %s at geo:%f,%f - Find me using the BeetRoute app http://goo.gl/BFqPj";

    private static final String TEMPLATE_LATLONG_ONLY = 
            "I am within %s meters of geo:%f,%f - Find me using the BeetRoute app http://goo.gl/BFqPj";

    
    public static String constructSMS(Context context, Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();

        // Try to get a name for the place
        GeocodingService geoService = new AndroidGeocodingService(context);
        SimpleGeoPoint sgPoint = new SimpleGeoPoint(location.getLatitude(), location.getLongitude());
        List<Address> addresses = geoService.resolveLocation(sgPoint, 1);
        
        if (addresses.size() <1) {
            // Couldn't find the place.  Just print the coordinates
            DecimalFormat df = new DecimalFormat("#####.#");
            String shortAccuracy = df.format(accuracy);
            String message = String.format(TEMPLATE_LATLONG_ONLY, 
                    shortAccuracy, latitude, longitude);
            
            return message;
        } else {
            // TODO: Try to find the best match from the address object
            Address address = addresses.get(0);
            String message = String.format(TEMPLATE_WITH_LOCATION, 
                    address.getFeatureName(), latitude, longitude);
            return message;
        }
    }

    /**
     * This function checks the text to see whether this is our geo-tagged
     * message
     * 
     * @param message
     * @return
     */
    public static boolean matchMessage(String message) {
        return (message.contains("Find me using the BeetRoute app")) ? true
                : false;
    }

    /**
     * This function extracts the location information from the geo-tagged
     * message
     * 
     * @param message
     * @return Location if succeeded, null otherwise
     */
    public static Location extractLocation(String message) {
        // TODO: there has to be a more efficient way of doing this, regex?

        // Get the first part after geo:
        // 37.786971,-122.399677 - Find me using the BeetRoute app
        // (http://goo.gl/BFqPj)
        String[] parts = message.split("geo:");
        if (parts.length >= 2) {
            String[] latlong = parts[1].split(" ");
            if (latlong.length >= 2) {
                String[] coords = latlong[0].split(",");
                if (coords.length == 2) {
                    Location location = new Location(Global.COMPANY);
                    location.setLatitude(Double.valueOf(coords[0]));
                    location.setLongitude(Double.valueOf(coords[1]));
                    // TODO: Extract and set the accuracy
                    return location;
                }
            }
        }
        return null;
    }

}
