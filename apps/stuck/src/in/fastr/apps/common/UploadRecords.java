package in.fastr.apps.common;

import java.io.IOException;
import java.io.InputStream;

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
 * @author gaurav
 *
 */
public class UploadRecords {

    // This is the dummy node server i've been testing with
    // private String nodeServer = "http://127.0.0.1:8124";
    private static String nodeServer = 
        "http://7310.basicserver.gauravlochan.test.jsapp.us/";
    private static String btis = "http://www.btis.in/trafficstatus_cache.txt";

    public static void upload() {
        callRest(nodeServer);
    }
    
    
    public static void callRest(String server) {
        Log.d("fastr", "Attempting call to REST");

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(server);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response != null) {
                Log.d("fastr", "Successful call to REST");
                Log.d("fastr", response.getStatusLine().toString());
                
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    int l;
                    byte[] tmp = new byte[2048];
                    while ((l = instream.read(tmp)) != -1) {
                        Log.d("fastr", String.format("%s", tmp));
                    }
                }
                
            } else {
                Log.d("fastr", "Unsuccessful call to REST");
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
}