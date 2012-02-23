package in.beetroute.apps.traffic.location;

import in.beetroute.apps.traffic.db.LocationDbHelper;

import java.util.List;


/**
 * A class that helps sync 
 * 
 * @author gauravlochan
 *
 */
public class SyncLocationDatabase {
    private final static int RECORDS_TO_UPLOAD_AT_ONCE = 10;
    
    /**
     * try to upload locations from the DB
     * 
     * @param installationId
     */
    public void asyncUpload(String installationId, LocationDbHelper dbHelper) {
        // TODO: try to acquire a lock to prevent multiple threads doing the upload
        // (think of whether this may block too many threads though)
        
        List <LocationUpdate> updates = 
                dbHelper.getUnsyncedLocationUpdates(RECORDS_TO_UPLOAD_AT_ONCE);
   
    }
    
}
