package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.Preferences;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.parse.Parse;

/**
 * A service that is responsible for getting location updates and then storing/uploading
 * them.
 * 
 * @author gauravlochan
 */
public class LocationService extends Service {
    private static final String TAG = Global.COMPANY;

    // Gps listener
    GpsLocationListener gpsLocationListener = new GpsLocationListener();

    // Network listener
    NetworkLocationListener netLocationListener = new NetworkLocationListener();

    // Wrapper around DB
    LocationDbHelper dbHelper = new LocationDbHelper(this, null);

    String installationId;
  
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Logger.debug(TAG, "Service onCreate");
        super.onCreate();

        // Initialize the installationID
        installationId = Preferences.getInstallationId(this);
        
        // Register for GPS provider updates
        gpsLocationListener.startGpsListening();
        
        // If GPS is off, then get cell/wifi updates.  
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            netLocationListener.startNetworkListening();
        }
                
        Parse.initialize(this, "VsbP7epJPb5KuHYIJtC1b730WLRgfEaHPPHULwRY", "3BDxDW4ex3girWsbvHppbeUc8AURVFkkbWorUMsM"); 

        // Poke the location uploader to kick off unsynced updates
        new SyncLocationDatabase().asyncUpload(installationId, dbHelper);
    }
    
    
    @Override
    public void onDestroy() {
        Logger.debug(TAG, "Service onDestroy");
        super.onDestroy();
        
        gpsLocationListener.stopGpsListening();
        netLocationListener.stopNetworkListening();
    }
    
    
    private class GpsLocationListener implements LocationListener {
        public void startGpsListening() {
            Logger.debug(TAG, "Start GPS Listener");

            long delay = 60 * 1000; // 1 minute updates
            float minDistance = 50; // 50 meters

            LocationManager locationManager = (LocationManager) 
                    LocationService.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    delay, minDistance, this);
        }
        
        public void stopGpsListening() {
            Logger.debug(TAG, "Stop GPS Listener");
            LocationManager locationManager = (LocationManager) 
                    LocationService.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
            
        }
        
        // TODO: try to do optimizations like only upload on network access
        @Override
        public void onLocationChanged(Location location) {
            Logger.debug(TAG, "Got an update");
            
            // Write this to the DB and Upload this location
            new StoreLocationTask(installationId, dbHelper).doInBackground(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.debug(TAG, "GPS Provider Status changed: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.debug(TAG, "GPS Provider enabled");
            netLocationListener.stopNetworkListening();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.debug(TAG, "GPS Provider disabled");
            netLocationListener.startNetworkListening();
        }
        
    }
    
    

    public class NetworkLocationListener implements LocationListener {
        public void startNetworkListening() {
            Logger.debug(TAG, "Start Net Listener");

            // Since cellID locations are much more inaccurate and are used for aggregate trips
            long delay = 5* 60 * 1000; // 5 minute updates
            float minDistance = 500; // 500 meters
            
            LocationManager locationManager = (LocationManager) 
                    LocationService.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    delay, minDistance, this);
        }
        
        public void stopNetworkListening() {
            Logger.debug(TAG, "Stop Net Listener");
            LocationManager locationManager = (LocationManager) 
                    LocationService.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }

        
        @Override
        public void onLocationChanged(Location location) {
            Logger.debug(TAG, "Got an update");
            
            // Write this to the DB and Upload this location
            new StoreLocationTask(installationId, dbHelper).doInBackground(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.debug(TAG, "Net Provider Status changed: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.debug(TAG, "Net Provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.debug(TAG, "Net Provider disabled");
        }
        
    }
    
}
