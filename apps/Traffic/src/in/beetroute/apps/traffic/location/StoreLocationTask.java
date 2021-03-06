package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.backend.ParseHelper;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import android.location.Location;
import android.os.AsyncTask;

/**
 * Takes in the location to upload.
 * Tries to upload and writes to DB
 * 
 * @author gauravlochan
 */
public class StoreLocationTask extends AsyncTask<Location, Void, Void> {
    private static final String TAG = Global.COMPANY;
    
    private String installationId;
    private LocationDbHelper dbHelper;
    private String listener;
    
    public StoreLocationTask(String _installationId, LocationDbHelper _dbHelper) {
        installationId = _installationId;
        dbHelper = _dbHelper;
        listener = null;
    }
    
    public StoreLocationTask(String _installationId, LocationDbHelper _dbHelper, String _listener) {
        installationId = _installationId;
        dbHelper = _dbHelper;
        listener = _listener;
    }

    @Override
    protected Void doInBackground(Location... params) {
        Logger.debug(TAG, "StoreLocationTask");
        
        Location location = params[0];
        LocationUpdate point = new LocationUpdate(location);

        // TODO: There is a chance we could lose the location altogether
        // But the alternative (storing it in DB first, then uploading, then updating the DB status)
        // is more work 
        try {
            if (listener == null) {
                ParseHelper.eventualLocationUpdate(location, installationId);
            } else {
                ParseHelper.eventualLocationUpdate(location, installationId, listener);
            }
            
            // Mark in DB with success
            dbHelper.insertPoint(point, true);
            
        } catch (Exception e) {
            // Mark in DB with failure
            dbHelper.insertPoint(point, false);

            Logger.info(TAG, "Inserted location to DB as 'Not Uploaded'");
        }
        
        return null;
    }
 
}
