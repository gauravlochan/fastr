package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;

/**
 * Static helped functions
 * 
 * @author gauravlochan
 */
public class LocationHelper {
    private static final String TAG = Global.COMPANY;
    private static final int ACCEPTABLE_AGE = 1000 * 60 * 5; // 5 minutes

    // TODO: Need to refine this function some more
    // http://developer.android.com/guide/topics/location/obtaining-user-location.html#BestEstimate
    public static Location getBestLocation(Context context) {
        Location location = getLastGpsLocation(context);
        if (location != null) {
            Date date = new Date();
            if (date.getTime() - location.getTime() < ACCEPTABLE_AGE) {
                return location;
            }
        }
        
        return getLastNetworkLocation(context);
    }

	/**
     * Get the last known location using the network provider
     * 
     * @param context
     */
    public static Location getLastNetworkLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    
    /**
     * Get the last known location using the GPS provider
     * 
     * @param context
     * @return
     */
    public static Location getLastGpsLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    
    
    /**
     * checks to see if the GPS provider is enabled
     * 
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) ? true : false;
    }


    /**
     * Checks to see if the device even supports a GPS provider
     * 
     * @param context
     * @return
     */
    public static boolean doesDeviceHaveGps(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        
        List<String> providers = locationManager.getAllProviders();
        return (providers.contains(LocationManager.GPS_PROVIDER)) ? true : false;
    }
    
    
    /**
     * Helper function to convert a Location to a GeoPoint
     * 
     * @param location
     * @return
     */
    public static GeoPoint locationToGeoPoint(Location location) {
    	Double latitude = location.getLatitude() * 1E6;
		Double longitude = location.getLongitude() * 1E6;

		GeoPoint locationPoint = new GeoPoint(latitude.intValue(),
				longitude.intValue());
		return locationPoint;
    }
}
