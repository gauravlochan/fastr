package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.Preferences;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import in.beetroute.apps.traffic.trip.Trip;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/** 
 * This is the low frequency listener (LFL)
 * This determines when the high frequency listener (HFL) should start.
 * 
 * @author gauravlochan
 */
class LowFrequencyListener implements LocationListener {
    private static final String TAG = Global.COMPANY;

    long delay = 180 * 1000; // 3 minute updates
    float minDistance = 500; // 500 meters

    final HighFrequencyUpdates highFrequencyUpdater;
    LocationManager locationManager;
    Context context;
    LocationDbHelper dbHelper;

    /** This is the assumed trip starting point */
    Location tripStartingPoint = null;
    
    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {  
            switch(msg.what) {
            case 0:
                LowFrequencyListener.this.startListening();
                break;
            }
        }

    };
    
    public LowFrequencyListener(Context context, LocationDbHelper dbHelper) {
        highFrequencyUpdater = new HighFrequencyUpdates(context, messageHandler, dbHelper);
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        this.dbHelper = dbHelper;
    }

    
    public void startListening() {
        Logger.info(TAG, "Start Low frequency Listener");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                delay, minDistance, this);
    }
    
    public void stopListening() {
        Logger.info(TAG, "Stop Low frequency Listener");
        if (highFrequencyUpdater.isListening) {
            highFrequencyUpdater.stopListening();
        } else {
            locationManager.removeUpdates(this);
        }
    }

    
    @Override
    public void onLocationChanged(Location location) {
        Logger.debug(TAG, "LFL: OnLocationChanged");

        if (location.getAccuracy() > Trip.MIN_ACCURACY) {
            Logger.debug(TAG,  "LFL: Ignoring update with accuracy = "+location.getAccuracy());
            return;
        }

        // Check to see whether we should start a HFL
        boolean startHFL = false;
        
        if (tripStartingPoint == null) {
            Logger.info(TAG,  "LFL: First update "+ location);
            tripStartingPoint = location;
        } else {
            // If this update is much newer than the trip starting point
            // then ignore that and start a new trip here
            if (tripStartingPoint.getTime() + Trip.TIME_CUTOFF < location.getTime()) {
                Logger.debug(TAG,  "LFL: ignore old location");
                tripStartingPoint = location;
            } else {
                // check to see if we moved since the 'trip' started
                if (isMoving(tripStartingPoint, location)) {
                    Logger.debug(TAG,  "LFL: we're moving");
                    startHFL = true;
                    tripStartingPoint = null;
                    // User is moving.  Start HFL and stop LFL
                    highFrequencyUpdater.startListening();
                    locationManager.removeUpdates(this);
                    Logger.debug(TAG, "LFL: unregister LFL updates");
                }
            }
        }
        
        // Write this to the DB and Upload this location
        String installationId = Preferences.getInstallationId(context);
        new StoreLocationTask(installationId, dbHelper, "LFL").doInBackground(location);
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
    
    /**
     * Determine whether a trip has started
     * 
     * @param lastMovingPoint
     * @param currentPoint
     * @return
     */
    static boolean isMoving(Location lastMovingPoint, Location currentPoint) {
        final float DIST_THRESHOLD = 0.500f; // 500 meters

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
        
        return false;
    }

}

