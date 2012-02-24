package in.beetroute.apps.commonlib;

import android.util.Log;

/**
 * This is a Logging wrapper.  Use this instead of the andoid class directly.
 * That way it's easier to test the java code on a non-android environment.
 * 
 * @author gauravlochan
 */

public class Logger {
    public static final boolean DEBUG = true;   // Change this when releasing the product
    public static final boolean JVM = false;    // Change this when testing in eclipse
    
    public static void debug(String tag, String message) {
        if (DEBUG) {
            if (JVM) {
                // don't log
            } else {
                Log.d(tag, message);
            }
        }
    }

    public static void info(String tag, String message) {
        if (DEBUG) {
            if (JVM) {
                // don't log
            } else {
                Log.i(tag, message);
            }
        }
    }

}
