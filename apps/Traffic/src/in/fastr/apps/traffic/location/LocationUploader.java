package in.fastr.apps.traffic.location;

import in.fastr.apps.traffic.backend.ParseHelper;
import android.location.Location;


public class LocationUploader {

    // This should be called asynchronously
    public static void upload(Location location, String installationId) {
        // try to acquire a lock to prevent multiple threads doing the upload
        // (think of whether this may block too many threads though)
        
        // Get a cursor of all unsynced records
//        return database.query(DATABASE_TABLE, 
//                new String[] { KEY_ROWID, KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION }, 
//                null, null, null, null, null);

        // Upload one at a time to parse
        // TODO: Make the parse upload synchronous
        ParseHelper.pushLocationUpdate(location, installationId);

        // If upload succeeds, mark the record as uploaded.
        // next record
        
        // release lock
        
    }
}
