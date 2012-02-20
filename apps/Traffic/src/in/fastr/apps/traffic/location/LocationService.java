package in.fastr.apps.traffic.location;

import in.fastr.apps.traffic.Preferences;
import in.fastr.apps.traffic.db.LocationDbHelper;
import in.fastr.library.Global;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.parse.Parse;

/**
 * A service that is responsible for getting location updates and then storing/uploading
 * them.
 * 
 * @author gauravlochan
 */
public class LocationService extends Service {

    // Define a listener that responds to location updates
    LocationListener locationListener = new MyLocationListener();
    
    LocationDbHelper dbHelper = new LocationDbHelper(this, null);
    String installationId;
  
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(Global.Company, "Service onCreate");
        super.onCreate();

        // Register the listener with the Location Manager to receive location updates
        LocationManager locationManager = (LocationManager) 
                this.getSystemService(Context.LOCATION_SERVICE);
        
        long delay = 60 * 1000; // 1 minute updates
        float minDistance = 50; // 50 meters

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                delay, minDistance, locationListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                delay, minDistance, locationListener);

        installationId = Preferences.getInstallationId(this);
      
        Parse.initialize(this, "VsbP7epJPb5KuHYIJtC1b730WLRgfEaHPPHULwRY", "3BDxDW4ex3girWsbvHppbeUc8AURVFkkbWorUMsM"); 

        // Poke the location uploader to kick off unsynced updates
        new SyncLocationDatabase().asyncUpload(installationId, dbHelper);
    }
    
    
    @Override
    public void onDestroy() {
        Log.d(Global.Company, "Service onDestroy");
        super.onDestroy();
        
        LocationManager locationManager = (LocationManager) 
                this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }
    
    
    public class MyLocationListener implements LocationListener {

        // TODO: try to do optimizations like only upload on network access
        @Override
        public void onLocationChanged(Location location) {
            Log.d(Global.Company, "Got an update");
            
            // Write this to the DB and Upload this location
            new StoreLocationTask(installationId, dbHelper).doInBackground(location);
            


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(Global.Company, "On Status changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(Global.Company, "Provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(Global.Company, "Provider disabled");
        }
        
    }
    
}
