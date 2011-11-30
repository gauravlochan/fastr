package in.fastr.apps.stuck;

import in.fastr.apps.common.CongestionPoint;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationRetriever {
    LocationManager locationManager;
    DbWrapper dbWrapper;

    public LocationRetriever(DbWrapper dbWrapper, LocationManager locationManager) {
        // TODO: Until DbWrapper can be disconnected from the Activity
        // this local variable needs to be passed in and stored
        this.dbWrapper = dbWrapper;
        this.locationManager = locationManager;
    }

    /**
     * Kick off a listener to get the GPS coordinate.
     * 
     * @param locationManager
     */
    public void startLocationQuery(Context context) {
        LocationListener locationListener = new SingleLocationListener();
        Log.d(App.Name, "Starting GPS Listener");
        
        String provider = LocationHelper.getAvailableProvider(context, locationManager);
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
    }
    
  
    public class SingleLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            CongestionPoint point = new CongestionPoint(
                    new Date().getTime(),
                    loc.getLatitude(), 
                    loc.getLongitude(),
                    loc.getSpeed(),
                    loc.getAccuracy());
            
            Log.d(App.Name, "GPS reports " + point.toString());
            
            // Now that we have a location, tell the manager to stop listening
            Log.d(App.Name, "Stopping GPS Listener");
            locationManager.removeUpdates(this);

            // And write this congestion point into the DB
            // TODO decide below which speed should the congestion point.
            if (point.getSpeed() < 5) {
                dbWrapper.insertPoint(point);
            }            
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

}
