package in.fastr.apps.traffic;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.os.Bundle;

public class LocationRetriever {
	Location location;

    /**
     * Get the last known location
     * 
     * @param locationManager
     */
    public Location getLastKnownLocation(LocationManager locationManager) {
	    String provider = LocationHelper.getDefaultProvider(locationManager);
        location = locationManager.getLastKnownLocation(provider);
        return location;
    }
	
	
    /**
     * Kick off a listener to get the GPS coordinate.
     * 
     * @param locationManager
     */
    public Location getCurrentLocation(LocationManager locationManager, Context context) {
	    // Define a listener that responds to location updates
	    LocationListener locationListener = new LocationListener() {
	        public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
	            LocationRetriever.this.location = location;
	        }
	
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	        public void onProviderEnabled(String provider) {}
	
	        public void onProviderDisabled(String provider) {}
	      };
	      
	    String provider;  
	    // provider = LocationManager.NETWORK_PROVIDER;
        provider = LocationHelper.getAvailableProvider(context, locationManager);
        // provider = LocationHelper.getDefaultProvider(locationManager);
        
	    // Register the listener with the Location Manager to receive location updates
	    locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
	    return location;
    }    
    
}
