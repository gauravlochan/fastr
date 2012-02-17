package in.fastr.apps.traffic;

import in.fastr.library.Global;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Wrapper class around preferences
 * 
 * @author gauravlochan
 */
public class Preferences {
    public static final String INSTALLATION_ID_KEY = "InstallationId";
    
    /**
     * Returns a unique key for this app installation
     * If a key doesnt exist, it creates one and sets in the preferences.
     * Ref: http://android-developers.blogspot.in/2011/03/identifying-app-installations.html
     * 
     * @param context
     * @return
     */
    public static String getInstallationId(Context context) {
        SharedPreferences app_preferences = 
                PreferenceManager.getDefaultSharedPreferences(context);
        
        if (app_preferences.contains(INSTALLATION_ID_KEY)) {
            return app_preferences.getString(INSTALLATION_ID_KEY, null);
        }

        // id doesnt exist, create a random one
        String id = UUID.randomUUID().toString();
        Log.i(Global.Company, "Generated an installation ID="+id);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(INSTALLATION_ID_KEY, id);
        editor.commit();
        
        // TODO: Add a timestamp for when this installation was done.

        return id;
    }

}
