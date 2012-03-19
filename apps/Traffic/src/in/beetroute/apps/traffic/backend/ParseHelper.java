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
    
    
    public static void eventualLocationUpdate(Location location, String installationId) throws ParseException {
        ParseObject parseObject = createParseObject(location, installationId);
        // TODO: Note, Parse takes care to upload this, or save it locally for later
        parseObject.saveEventually();
        Logger.debug(TAG, "Initiated eventual upload to Parse");
    }
    
    private static ParseObject createParseObject(Location location, String installationId) {
        // TODO TESTING DONT RELEASE WITH THIS
        ParseObject testObject = new ParseObject("LocationUpdateTest");
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        
        testObject.put("location", geoPoint);
        testObject.put("timestamp", location.getTime());
        testObject.put("speed", location.getSpeed());
        testObject.put("accuracy", location.getAccuracy());
        testObject.put("bearing", location.getBearing());
        testObject.put("installationId", installationId);

        return testObject;
    }
}
