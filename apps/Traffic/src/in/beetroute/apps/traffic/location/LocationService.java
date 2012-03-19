package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
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
 * Look at algorithm.txt for details on this
 * 
 * @author gauravlochan
 */
public class LocationService extends Service {
    private static final String TAG = Global.COMPANY;
    
    // The thresholds that are used to calculate the end of the trip
    private static final int TIME_CUTOFF = 10 * 60 * 1000; // 10 minutes
    private static final float DIST_THRESHOLD = 0.500f; // 500 meters
    private static final float SPEED_THRESHOLD = 1.0f; // 1 m/s = 3.6 km/hr 

    LowFrequencyListener lowFrequencyListener;
    
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
        
        // Register for infrequent low-power updates
        lowFrequencyListener = new LowFrequencyListener(this);
        lowFrequencyListener.startListening();
        
        Parse.initialize(this, "VsbP7epJPb5KuHYIJtC1b730WLRgfEaHPPHULwRY", "3BDxDW4ex3girWsbvHppbeUc8AURVFkkbWorUMsM"); 

        // Poke the location uploader to kick off unsynced updates
        new SyncLocationDatabase().asyncUpload(installationId, dbHelper);
    }
    
    
    @Override
    public void onDestroy() {
        Logger.debug(TAG, "Service onDestroy");
        super.onDestroy();
        
        lowFrequencyListener.stopListening();
    }
    
    
    /**
     * Algo: How to decide whether a user was moving, by looking at 2 location updates
     *    (point2.distance(point1) > cutoff, point2.speed = 0)
     * 
     * @param lastMovingPoint
     * @param currentPoint
     * @return
     */
    public static boolean isMoving(Location lastMovingPoint, Location currentPoint) {
        double distance = SimpleGeoPoint.getDistance(
                lastMovingPoint.getLatitude(), lastMovingPoint.getLongitude(),
                currentPoint.getLatitude(), currentPoint.getLongitude() );

        Logger.debug(TAG, String.format("Distance = %f between %f,%f to %f,%f", 
                distance,
                lastMovingPoint.getLatitude(), lastMovingPoint.getLongitude(),
                currentPoint.getLatitude(), currentPoint.getLongitude()));

        if (distance > DIST_THRESHOLD) {
            return true;
        }
        
        if (currentPoint.getSpeed() > SPEED_THRESHOLD) {
            return true;
        }
        
        return false;
    }
    

    
    /** 
     * This is the low frequency listener (LFL)
     * This determines when the high frequency listener (HFL) should start.
     * 
     * @author gauravlochan
     */
    class LowFrequencyListener implements LocationListener {
        long delay = 300 * 1000; // 5 minute updates
        float minDistance = 500; // 500 meters

        final HighFrequencyUpdates highFrequencyUpdater;
        LocationManager locationManager;

        /** This is the assumed trip starting point */
        Location tripStartingPoint = null;
        
        
        public LowFrequencyListener(Context context) {
            highFrequencyUpdater = new HighFrequencyUpdates(context, this, dbHelper);
            locationManager = (LocationManager)
                    LocationService.this.getSystemService(Context.LOCATION_SERVICE);
        }

        
        public void startListening() {
            Logger.debug(TAG, "Start Low frequency Listener");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    delay, minDistance, this);
        }
        
        public void stopListening() {
            Logger.debug(TAG, "Stop Low frequency Listener");
            if (highFrequencyUpdater.isListening) {
                highFrequencyUpdater.stopListening();
            } else {
                locationManager.removeUpdates(this);
            }
        }

        
        @Override
        public void onLocationChanged(Location location) {
            Logger.debug(TAG, "LFL: OnLocationChanged");
            
            if (location.getAccuracy() < minDistance) {
                Logger.debug(TAG,  "LFL: Ignoring update with accuracy = "+location.getAccuracy());
                return;
            }

            // Check to see whether we should start a HFL
            // boolean startHFL = false;
            // TODO TESTING
            boolean startHFL = true;
            
            if (tripStartingPoint == null) {
                tripStartingPoint = location;
            } else {
                // If this update is much newer than the trip starting point
                // then ignore that and start a new trip here
                if (tripStartingPoint.getTime() + TIME_CUTOFF < location.getTime()) {
                    tripStartingPoint = location;
                } else {
                    // check to see if we moved since the 'trip' started
                    if (LocationService.isMoving(tripStartingPoint, location)) {
                        startHFL = true;
                        tripStartingPoint = null;
                    }
                }
            }

            if (startHFL) {
                // User is moving.  Start HFL and stop LFL
                highFrequencyUpdater.startListening();
                locationManager.removeUpdates(this);
                Logger.debug(TAG, "LFL: unregister LFL updates");
            }
            
            // Write this to the DB and Upload this location
            // TODO: Should we only upload locations from HFL?
            new StoreLocationTask(installationId, dbHelper).doInBackground(location);
        }

        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.debug(TAG, "LFL: OnStatusChanged " + status);
            // No need to do anything here.
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.debug(TAG, "LFL: OnProviderEnabled");
            // No need to do anything here.
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.debug(TAG, "LFL: OnProviderDisabled");
            // No need to do anything here.
        }
    }

    
}
