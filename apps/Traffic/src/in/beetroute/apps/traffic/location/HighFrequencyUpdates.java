package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.Preferences;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import in.beetroute.apps.traffic.trip.Trip;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * This kicks off a GPS listener
 * 
 * @author gauravlochan
 */
public class HighFrequencyUpdates {
    private static final String TAG = Global.COMPANY;
    final long delay = 60 * 1000; // 1 minute updates
    final float minDistance = 100f; // 100 meters

    // Gps listener
    final GpsLocationListener gpsListener = new GpsLocationListener();

    // Network listener
    final NetworkLocationListener netListener = new NetworkLocationListener();

    // Reference back to the low frequency listener to call when we're stopped
    final private Handler lflHandler;
    final LocationDbHelper dbHelper;
    final String installationId;
    final LocationManager locationManager;
    final private Timer timer;
    
    // The timer task the decides when to stop high frequency listening
    protected NotMovingTimerTask timerTask = null;

    // the state of this updater.
    boolean isListening = false; 

    // the last detected moving point
    Location lastMovingPoint = null;
    
    
    public HighFrequencyUpdates(Context context, Handler lflHandler, LocationDbHelper dbHelper) {
        this.lflHandler = lflHandler;
        this.dbHelper = dbHelper;
        installationId = Preferences.getInstallationId(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        timer = new Timer();
    }
    
    public void startListening() {
        Logger.debug(TAG, "Start HighFrequencyUpdates");

        // Register for GPS provider updates
        gpsListener.start();
        
        // If GPS is off, then get cell/wifi updates.  
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            netListener.start();
        }
        
        scheduleTimerTask();
        isListening = true;
    }
    
    
    public void stopListening() {
        Logger.debug(TAG, "Stop HighFrequencyUpdates");
        
        gpsListener.stop();
        netListener.stop();
        isListening = false;
        
        // in case this was invoked during cleanup (and not from timerTask itself)
        resetTimerTask();
        lastMovingPoint = null;

        // Now hand things back to the low frequency listener
        lflHandler.sendEmptyMessage(0);
    }
    
    private void scheduleTimerTask() {
        timerTask = new NotMovingTimerTask();
        timer.schedule(timerTask, Trip.TIME_CUTOFF); // 10 minutes
        Logger.debug(TAG, "Scheduled timerTask");
    }
    
    private void resetTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timer.purge();
        Logger.debug(TAG, "Cancelled timerTask");
    }
    
    /**
     * Common onUpdate for both listeners.
     * 
     * (could have had them both derive from an abstract base class but that was
     *  overkill)
     * @param location
     */
    protected void onLocationChanged(Location location) {
        if (lastMovingPoint == null) {
            lastMovingPoint = location;
        } else {
            // if the user is actually moving, then save the last moving point
            if (Trip.isMoving(lastMovingPoint, location)) {
                Logger.debug(TAG, "Detected movement from " + lastMovingPoint + " to "+ location);
                lastMovingPoint = location;
                resetTimerTask();
                scheduleTimerTask();
            } else {
                Logger.debug(TAG, "No motion from " + lastMovingPoint + " to "+ location);
            }
        }
        
        // Write this to the DB and Upload this location
        new StoreLocationTask(installationId, dbHelper).doInBackground(location);
    }
    
    
    
    /**
     * This timerTask is called when we haven't moved for 10 minutes
     * 
     * @author gauravlochan
     */
    class NotMovingTimerTask extends TimerTask {
        @Override
        public void run() {
            Logger.info(TAG, "Not moving.  Switch out of High frequency updates");
            HighFrequencyUpdates.this.timerTask = null;
            
            // TODO: Can't invoke this directly, use a handler
            HighFrequencyUpdates.this.stopListening();
        }
    }

    
    /**
     * GPS Listener is always on (when we are in high frequency mode) since this is the
     * most reliable way to figure out when the GPS is turned on.
     * 
     * @author gauravlochan
     */
    private class GpsLocationListener implements LocationListener {
        public void start() {
            Logger.debug(TAG, "GpsListener: start");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    delay, minDistance, this);
        }

        public void stop() {
            Logger.debug(TAG, "GpsListener: stop");
            locationManager.removeUpdates(this);
        }

        @Override
        public void onLocationChanged(Location location) {
            Logger.debug(TAG, "GpsListener: Got an update");
            HighFrequencyUpdates.this.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.debug(TAG, "GpsListener: Status changed: " + status);
            // no need to do anything
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.debug(TAG, "GpsListener: enabled");
            netListener.stop();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.debug(TAG, "GpsListener: disabled");
            netListener.start();
        }
        
    }
    

    public class NetworkLocationListener implements LocationListener {
        public void start() {
            Logger.debug(TAG, "NetListener: start");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    delay, minDistance, this);
        }

        public void stop() {
            Logger.debug(TAG, "NetListener: stop");
            locationManager.removeUpdates(this);
        }
        
        @Override
        public void onLocationChanged(Location location) {
            Logger.debug(TAG, "NetListener: Got an update");
            
            if (location.getAccuracy() < Trip.MIN_ACCURACY) {
                Logger.debug(TAG,  "Ignoring update with accuracy = "+location.getAccuracy());
                return;
            }

            HighFrequencyUpdates.this.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.debug(TAG, "NetListener: Status changed: " + status);
            // no need to do anything
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.debug(TAG, "NetListener: enabled");
            // no need to do anything
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.debug(TAG, "NetListener: disabled");
            // no need to do anything
        }
    }

}