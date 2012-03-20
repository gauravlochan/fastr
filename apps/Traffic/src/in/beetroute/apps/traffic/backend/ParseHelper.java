package in.beetroute.apps.traffic.backend;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import android.location.Location;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseHelper {
    private static final String TAG = Global.COMPANY;

    public static void locationUpdate(Location location, String installationId) throws ParseException {
        ParseObject parseObject = createParseObject(location, installationId);
        parseObject.save();
        Logger.debug(TAG, "Completed upload to Parse");
    }

    public static void backgroundLocationUpdate(Location location, String installationId) {
        ParseObject parseObject = createParseObject(location, installationId);
        parseObject.saveInBackground();
        Logger.debug(TAG, "Kicked off upload to Parse");
    }
    
    
    public static void eventualLocationUpdate(Location location, String installationId) 
            throws ParseException {
        ParseObject parseObject = createParseObject(location, installationId);
        parseObject.saveEventually();
        Logger.debug(TAG, "Initiated eventual upload to Parse");
    }
    
    public static void eventualLocationUpdate(Location location, String installationId, String listener) 
            throws ParseException {
        ParseObject parseObject = createParseObject(location, installationId, listener);
        parseObject.saveEventually();
        Logger.debug(TAG, "Initiated eventual upload to Parse");
    }
    
    /** Create a Parse object to be stored in the LocationUpdate table
     * 
     * @param location
     * @param installationId
     * @return
     */
    private static ParseObject createParseObject(Location location, String installationId) {
        ParseObject testObject = new ParseObject("LocationUpdate");
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        
        testObject.put("location", geoPoint);
        testObject.put("timestamp", location.getTime());
        testObject.put("speed", location.getSpeed());
        testObject.put("accuracy", location.getAccuracy());
        testObject.put("bearing", location.getBearing());
        testObject.put("installationId", installationId);

        return testObject;
    }


    /**
     * Create a Parse object to be stored in the new LocationUpdate table
     * This version also stores whether the update is coming from HFL or LFL
     * 
     * @param location
     * @param installationId
     * @return
     */
    private static ParseObject createParseObject(Location location, String installationId,
            String listener) {
        ParseObject testObject = new ParseObject("LocationUpdate2");
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        
        testObject.put("location", geoPoint);
        testObject.put("timestamp", location.getTime());
        testObject.put("speed", location.getSpeed());
        testObject.put("accuracy", location.getAccuracy());
        testObject.put("bearing", location.getBearing());
        testObject.put("installationId", installationId);
        testObject.put("listener", listener);

        return testObject;
    }
}
