package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class OneTimeGpsLocationListener implements LocationListener {
    private static final String TAG = Global.COMPANY;
    private static final long WAIT_LIMIT = 120 * 1000; // 2 minutes
    
    private Location location = null;
    private final LocationManager locationManager;

    /**
     * Assumes GPS is enabled.  The current workflow does it before calling this code
     * This needs to be called in the UI thread
     */
    public OneTimeGpsLocationListener(LocationManager manager) {
        locationManager = manager;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    /**
     * This should not be called in the Ui thread
     * @return
     */
    public Location waitForLocation() {
        long startTime = System.currentTimeMillis();
        
        // Keep waiting until we get a location update
        while (location == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.error(TAG, "Error waiting for location update", e);
            }
            if (System.currentTimeMillis() > startTime + WAIT_LIMIT) {
                Logger.debug(TAG, "OneTimeGpsListener: didn't get GPS fix in time");
                return null;
            }
        }
        return location;
    }
    
    @Override
    public void onLocationChanged(Location location) {
        Logger.debug(TAG, "OneTimeGpsListener: Got an update "+location);
        locationManager.removeUpdates(this);
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Logger.debug(TAG, "OneTimeGpsListener: Status changed: " + status);
        // no need to do anything
    }

    @Override
    public void onProviderEnabled(String provider) {
        Logger.debug(TAG, "OneTimeGpsListener: enabled");
        // no need to do anything
    }

    @Override
    public void onProviderDisabled(String provider) {
        Logger.debug(TAG, "OneTimeGpsListener: disabled");
        // no need to do anything
    }

}
