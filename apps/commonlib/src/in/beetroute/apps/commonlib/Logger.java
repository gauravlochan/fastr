package in.beetroute.apps.commonlib;

import android.util.Log;

/**
 * This is a Logging wrapper.  Use this instead of the andoid class directly.
 * That way it's easier to test the java code on a non-android environment.
 * 
 * @author gauravlochan
 */

public class Logger {
    // Only log statements that are higher than this level
    // Development: set this to Log.DEBUG
    // Release: Set this to Log.INFO
    public static final int LOG_LEVEL = Log.INFO;
    public static final boolean JVM = false;    // Change this when testing in eclipse
    
    public static int verbose(String tag, String message) {
        if (LOG_LEVEL <= Log.VERBOSE) {
            if (JVM) {
                // don't log
                return 0;
            } else {
                return Log.v(tag, message);
            }
        }
        // don't log for release builds
        return 0;
    }

    
    public static int debug(String tag, String message) {
        if (LOG_LEVEL <= Log.DEBUG) {
            if (JVM) {
                // don't log
                return 0;
            } else {
                return Log.d(tag, message);
            }
        }
        // don't log for release builds
        return 0;
    }

    
    public static int info(String tag, String message) {
        if (LOG_LEVEL <= Log.INFO) {
            if (JVM) {
                // don't log
                return 0;
            } else {
                return Log.i(tag, message);
            }
        }
        // don't log for release builds
        return 0;
    }
    
    public static int warn(String tag, String message) {
        // warnings should be logged regardless of debug/release
        if (JVM) {
            return 0;
            // don't log
        } else {
            return Log.w(tag, message);
        }
    }
    
    public static int error(String tag, String message, Throwable tr) {
        // errors should be logged regardless of debug/release
        if (JVM) {
            return 0;
            // don't log
        } else {
            return Log.e(tag, message, tr);
        }
    }
    
    public static int error(String tag, String message) {
        // errors should be logged regardless of debug/release
        if (JVM) {
            return 0;
            // don't log
        } else {
            return Log.e(tag, message);
        }
    }

}
