package in.beetroute.apps.traffic.location;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.Preferences;
import in.beetroute.apps.traffic.db.LocationDbHelper;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.parse.Parse;

/**
 * A service that is responsible for getting location updates and then storing/uploading
 * them.
 * 
 * Look at algorithm.txt for details on this
 * 
 * @author gauravlochan
 */
public class LocationService extends Service {
    private static final String TAG = Global.COMPANY;
    
    LowFrequencyListener lowFrequencyListener;
    
    LocationDbHelper dbHelper = new LocationDbHelper(this, null);
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Logger.debug(TAG, "Service onCreate");
        super.onCreate();

        // Initialize the installationID
        String installationId = Preferences.getInstallationId(this);
        
        // Register for infrequent low-power updates
        lowFrequencyListener = new LowFrequencyListener(this, dbHelper);
        lowFrequencyListener.startListening();
        
        Parse.initialize(this, "VsbP7epJPb5KuHYIJtC1b730WLRgfEaHPPHULwRY", "3BDxDW4ex3girWsbvHppbeUc8AURVFkkbWorUMsM"); 

        // Poke the location uploader to kick off unsynced updates
        new SyncLocationDatabase().asyncUpload(installationId, dbHelper);
    }
    
    
    @Override
    public void onDestroy() {
        Logger.debug(TAG, "Service onDestroy");
        super.onDestroy();
        
        lowFrequencyListener.stopListening();
    }

    
    
}
