package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.Preferences;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import in.beetroute.apps.traffic.location.LocationService.LowFrequencyListener;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * This kicks off a GPS listener
 * 
 * @author gauravlochan
 */
public class HighFrequencyUpdates implements GpsStatus.Listener {
    private static final String TAG = Global.COMPANY;

    long delay = 60 * 1000; // 1 minute updates
    float minDistance = 100; // 100 meters
    boolean isListening = false; 

    Context context;
    private LowFrequencyListener lowFrequencyListener;
    private HighFrequencyListener highFrequencyListener;
    LocationManager locationManager;

    LocationDbHelper dbHelper;
    String installationId;
    
    
    public HighFrequencyUpdates(Context context, LowFrequencyListener lfl, LocationDbHelper dbHelper) {
        this.context = context;
        this.lowFrequencyListener = lfl;
        this.dbHelper = dbHelper;
        
        highFrequencyListener = new HighFrequencyListener();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        installationId = Preferences.getInstallationId(context);

    }
    
    public void startListening() {
        Logger.debug(TAG, "Start HighFrequencyUpdates");

        // If GPS is on, then use that
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    delay, minDistance, highFrequencyListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    delay, minDistance, highFrequencyListener);
        }
        isListening = true;
        
        boolean added = locationManager.addGpsStatusListener(this);
        Logger.debug(TAG, "added gps status listener "+added);
    }
    
    
    public void stopListening() {
        Logger.debug(TAG, "Stop HighFrequencyUpdates");
        
        // defensive check.
        if (!isListening) {
            Logger.warn(TAG, "Not listening and yet asked to stop listening");
            return;
        }
        
        locationManager.removeUpdates(highFrequencyListener);
        isListening = false;
        
        locationManager.removeGpsStatusListener(this);

        // Now hand things back to the low frequency listener
        lowFrequencyListener.startListening();
    }
    
    // http://stackoverflow.com/questions/2021176/how-can-i-check-the-current-status-of-the-gps-receiver
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Logger.debug(TAG, "HighFrequencyUpdates: GPS started, use it");
                locationManager.removeUpdates(highFrequencyListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        delay, minDistance, highFrequencyListener);

                break;
            case GpsStatus.GPS_EVENT_STOPPED: 
                Logger.debug(TAG, "HighFrequencyUpdates: GPS stopped, switch to network");
                locationManager.removeUpdates(highFrequencyListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        delay, minDistance, highFrequencyListener);

                break;
        }
    }


    class HighFrequencyListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Logger.debug(TAG, "HFL: OnLocationChanged");
            
            // TODO: Check if time to turn this off
            
            // Write this to the DB and Upload this location
            new StoreLocationTask(installationId, dbHelper).doInBackground(location);
        }
   
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.debug(TAG, "HFL: OnStatusChanged " + status);
            // No need to do anything here.
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.debug(TAG, "HFL: OnProviderEnabled");
            // No need to do anything here.
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.debug(TAG, "HFL: OnProviderDisabled");
            // No need to do anything here.
        }
    }

}