package in.beetroute.apps.commonlib;

/**
 * This is a Logging wrapper interface.  Use this instead of the andoid class directly.
 * That way it's easier to test the java code on a non-android environment.
 * 
 * @author gauravlochan
 */
public interface Logger {
    
    public void debug(String message);
    public void info(String message);

}
