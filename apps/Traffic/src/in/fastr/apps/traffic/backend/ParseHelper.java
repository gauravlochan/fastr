package in.fastr.apps.traffic.backend;

import in.fastr.library.Global;
import android.location.Location;
import android.util.Log;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseHelper {

    public static void pushLocationUpdate(Location location, String installationId) {
        ParseObject testObject = new ParseObject("LocationUpdate");
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        
        testObject.put("location", geoPoint);
        testObject.put("timestamp", location.getTime());
        testObject.put("speed", location.getSpeed());
        testObject.put("accuracy", location.getAccuracy());
        testObject.put("bearing", location.getBearing());
        testObject.put("installationId", installationId);
        
        testObject.saveInBackground();
        Log.d(Global.Company, "Kicked off upload to Parse");
    }
}
