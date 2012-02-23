package in.beetroute.apps.traffic;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.traffic.location.LocationService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = Global.COMPANY;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent myIntent = new Intent(context, LocationService.class);
            context.startService(myIntent);
        }
        
        
        // android.net.ConnectivityManager.CONNECTIVITY_ACTION
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            Log.d(TAG, "Connectivity Change");
        }

        
    }

}
