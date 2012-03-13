package in.beetroute.apps.commonlib;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

public class RESTHelper {
    public static final String TAG = Global.COMPANY;
	
    /**
     * A simple method to make a REST call to the specified server.  Good for 
     * testing
     * 
     * @param server
     */
    public static String simplePost(String server, JSONArray payload) {
    	return simplePost(server, payload.toString());
    }
    
	public static String simplePost(String server, String payload) {
		String result = null;
    	Logger.debug(TAG, "Attempting call to REST: " + server);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(server);
        
        HttpEntity requestEntity = null;
        try {
            requestEntity = new StringEntity(payload);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        httpPost.setEntity(requestEntity);

        try {
            HttpResponse response = null;
            response = httpClient.execute(httpPost);
            
            if (response != null) {
                Logger.debug(TAG, "Successful call to REST");
                Logger.debug(TAG, response.getStatusLine().toString());
                
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = convertStreamToString(instream);
                    // Logger.debug(TAG, "Result of conversation: [" + result + "]");
                    instream.close();
                } else {
                    Logger.debug(TAG, "Empty Http response");
                }
            } else {
                Logger.debug(TAG, "Unsuccessful call to REST");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
        
    }
    
    
    /**
     * A simple get call to the specified server.  Good for testing the 
     * server connectivity
     * 
     * @param server
     */
    public static String simpleGet(String server) {
        String result = null;
        Logger.debug(TAG, "Attempting call to REST: "+ server);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(server);
        try {
            HttpResponse response = null;
            response = httpClient.execute(httpGet);
            if (response != null) {
                Logger.debug(TAG, "Successful call to REST");
                Logger.debug(TAG, response.getStatusLine().toString());
                
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = convertStreamToString(instream);
                    // Logger.info(TAG, "Result of conversation: [" + result + "]");
                    instream.close();
                } else {
                    Logger.debug(TAG, "Empty Http response");
                }
            } else {
                Logger.debug(TAG, "Unsuccessful call to REST");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
