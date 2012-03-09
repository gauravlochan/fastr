package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;

import java.text.DecimalFormat;

import android.location.Location;

// http://en.wikipedia.org/wiki/Geo_URI
// e.g. geo:37.786971,-122.399677;u=35 (where u is uncertainty in meters)
public class GeoSMS {

    public static String constructSMS(Location location) {
        // TODO: Reverse Geocode to put a location name also
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();

        DecimalFormat df = new DecimalFormat("#####.#");
        String shortAccuracy = df.format(accuracy);

        String message = String.format(
                "I am within %s meters of geo:%f,%f;u=%s . Find me using the BeetRoute app (http://goo.gl/BFqPj).",
                shortAccuracy, latitude, longitude, shortAccuracy);
        return message;
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
     * @return
     */
    public static Location extractLocation(String message) {
        // TODO: there has to be a more efficient way of doing this, regex?

        // Get the first part after geo:
        // 37.786971,-122.399677;u=35 .Find me using the BeetRoute app
        // (http://goo.gl/BFqPj)
        String[] parts = message.split("geo:");
        if (parts.length >= 2) {
            String[] latlong = parts[1].split(";");
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
