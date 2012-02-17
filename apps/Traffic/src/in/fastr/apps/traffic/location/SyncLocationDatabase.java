package in.fastr.apps.traffic.location;



public class SyncLocationDatabase {
    
    /**
     * try to upload locations from the DB
     * 
     * @param installationId
     */
    public void asyncUpload(String installationId) {
        // try to acquire a lock to prevent multiple threads doing the upload
        // (think of whether this may block too many threads though)
        
        
        // Get a cursor of all unsynced records
//      return database.query(DATABASE_TABLE, 
//              new String[] { KEY_ROWID, KEY_CATEGORY, KEY_SUMMARY, KEY_DESCRIPTION }, 
//              null, null, null, null, null);
   
    }
    
}
