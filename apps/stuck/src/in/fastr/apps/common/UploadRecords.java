package in.fastr.apps.common;

import in.fastr.apps.stuck.DbWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
        "http://7310.basicserver.gauravlochan.test.jsapp.us/";
    private static String btis = "http://www.btis.in/trafficstatus_cache.txt";

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

        // Mark records as uploaded in the DB
        
        callRest(nodeServer);
    }
    
    /**
     * A simple method to make a REST call to the specified server.  Good for 
     * testing
     * 
     * @param server
     */
    public static void callRest(String server) {
        Log.d(Global.Company, "Attempting call to REST");

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(server);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response != null) {
                Log.d(Global.Company, "Successful call to REST");
                Log.d(Global.Company, response.getStatusLine().toString());
                
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = convertStreamToString(instream);
                    Log.i(Global.Company, "Result of converstion: [" + result + "]");
                    instream.close();
                } else {
                    Log.d(Global.Company, "Empty Http response");
                }
            } else {
                Log.d(Global.Company, "Unsuccessful call to REST");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
   
}