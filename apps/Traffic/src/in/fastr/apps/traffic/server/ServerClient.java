package in.fastr.apps.traffic.server;

import in.fastr.apps.traffic.services.Route;
import in.fastr.library.RESTHelper;

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
    	Gson gson = new Gson();
    	String jsonOutput = gson.toJson(route.getPoints());
    	
    	String result = RESTHelper.simplePost(serverIP, jsonOutput);
    	String test = "test";
	}

}
