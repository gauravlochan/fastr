package in.fastr.apps.traffic.backend;

import in.fastr.library.Global;
import android.location.Location;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseHelper {

    public static void asyncLocationUpdate(Location location, String installationId) {
        ParseObject parseObject = createParseObject(location, installationId);
        parseObject.saveInBackground();
        Log.d(Global.Company, "Kicked off upload to Parse");
    }
    
    public static void locationUpdate(Location location, String installationId) throws ParseException {
        ParseObject parseObject = createParseObject(location, installationId);
        parseObject.save();
        Log.d(Global.Company, "Completed upload to Parse");
    }
    
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
}
