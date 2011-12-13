package in.fastr.apps.traffic.server;

import in.fastr.apps.traffic.services.Route;
import in.fastr.library.RESTHelper;
import android.os.AsyncTask;

import com.google.gson.Gson;

/**
 * Methods that will be called against our own server
 * 
 * @author gauravlochan
 * 
 */
public class ServerClient {
	public static String serverIP = "http://192.168.2.107:8080/upload";
	
	public void sendRoute(Route route) {
		new SendRouteTask().execute(route);
	}
	
	// Task so as not to block the UI thread
	public class SendRouteTask extends AsyncTask<Route, Void, Void> {

		@Override
		protected Void doInBackground(Route... params) {
			Route route = params[0];
	    	Gson gson = new Gson();
	    	String jsonOutput = gson.toJson(route.getPoints());
	    	
	    	String result = RESTHelper.simplePost(serverIP, jsonOutput);
	    	String test = "test";
	    	
			return null;
		}

		
	}
}
