package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.traffic.backend.ParseHelper;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

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
    
    public StoreLocationTask(String _installationId, LocationDbHelper _dbHelper) {
        installationId = _installationId;
        dbHelper = _dbHelper;
    }

    @Override
    protected Void doInBackground(Location... params) {
        Log.d(TAG, "StoreLocationTask");
        
        Location location = params[0];
        LocationUpdate point = new LocationUpdate(location);

        // TODO: There is a chance we could lose the location altogether
        // But the alternative (storing it in DB first, then uploading, then updating the DB status)
        // is more work 
        try {
            ParseHelper.eventualLocationUpdate(location, installationId);
            
            // Mark in DB with success
            dbHelper.insertPoint(point, true);
            
            Log.d(TAG, "Inserted to DB as 'Uploaded'");
        } catch (Exception e) {
            // Mark in DB with failure
            dbHelper.insertPoint(point, false);

            Log.i(TAG, "Inserted location to DB as 'Not Uploaded'");
        }
        
        return null;
    }
 
}