package in.fastr.apps.traffic.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;

public class LocationHelper {
	
    /**
     * Gets a location provider, without any criteria
     * @param locationManager
     * @return
     */
	public static String getDefaultProvider(LocationManager locationManager) {
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		return provider;
	}


	/**
	 * Gets a provider, depending on what permissions the application has.
	 * 
	 * @param context
	 * @param locManager
	 * @return
	 */
    public static String getAvailableProvider(Context context, 
            LocationManager locManager) {
        
        Context cntx = context;
        LocationManager locationManager = locManager;

        String provider = "";
        Criteria criteria = new Criteria();
        if (cntx.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setSpeedRequired(true);
            criteria.setBearingRequired(true);
        } else if (cntx.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, true);
        return provider;
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
