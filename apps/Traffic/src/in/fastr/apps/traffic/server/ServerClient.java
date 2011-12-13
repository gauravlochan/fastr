package in.fastr.apps.traffic.server;

import in.fastr.apps.traffic.Route;
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
	// This is the address of pradeeps server.  Need to use a public server soon!
	public static String serverIP = "http://192.168.1.4:8888/upload";
	
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
