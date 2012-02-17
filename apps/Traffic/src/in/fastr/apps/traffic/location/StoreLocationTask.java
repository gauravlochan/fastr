package in.fastr.apps.traffic.location;

import in.fastr.apps.traffic.backend.ParseHelper;
import in.fastr.apps.traffic.db.LocationDbHelper;
import android.location.Location;
import android.os.AsyncTask;

/**
 * Takes in the location to upload.
 * Tries to upload and writes to DB
 * 
 * @author gauravlochan
 */
public class StoreLocationTask extends AsyncTask<Location, Void, Void> {
    private String installationId;
    private LocationDbHelper dbHelper;
    
    public StoreLocationTask(String _installationId, LocationDbHelper _dbHelper) {
        installationId = _installationId;
        dbHelper = _dbHelper;
    }

    @Override
    protected Void doInBackground(Location... params) {
        Location location = params[0];
        LocationUpdate point = new LocationUpdate(location);

        // TODO: There is a tiny chance we could lose the location altogether
        // But the alternative (storing it in DB first, then uploading, then updating the DB status)
        // was more work 
        try {
            ParseHelper.locationUpdate(location, installationId);
            
            // Mark in DB with success
            dbHelper.insertPoint(point, true);
        } catch (Exception e) {
            // Mark in DB with failure
            dbHelper.insertPoint(point, false);
        
            // Log error
        }
        
        return null;
    }
 
}
