package in.fastr.apps.common;

import in.fastr.apps.stuck.DbWrapper;

import java.util.List;
import org.json.JSONArray;

import android.util.Log;

/**
 * Class needs to upload datapoints to the server.  On successfully uploading,
 * remove those datapoints from the local DB.
 *
 * TODO: Batch records if there are too many to upload at once
 * 
 * @author gaurav
 *
 */
public class UploadRecords {
    // This is the dummy node server i've been testing with
    // private String nodeServer = "http://127.0.0.1:8124";
    private static String nodeServer = 
        "http://5270.basicserver.gauravlochan.test.jsapp.us/";

    public static void upload(DbWrapper dbWrapper) { 
        // Are there any new records? If not, quit
        if (dbWrapper.countUnsyncedCongestionPoints() == 0) {
            Log.d(Global.Company, "No records for upload");
            return;
        }
        
        // Try to upload all the new records
        List<CongestionPoint> congestionPoints = 
            dbWrapper.getUnsyncedCongestionPoints();
        
        // Convert records into JSON for REST
        JSONArray array = new JSONArray(congestionPoints);
        
        // Mark records as uploaded in the DB
        RESTHelper.simplePost(nodeServer, array);
    }
    
  
}