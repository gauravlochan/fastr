package in.fastr.apps.traffic;

import in.fastr.apps.traffic.location.LocationService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent myIntent = new Intent(context, LocationService.class);
            context.startService(myIntent);
        }
    }

}
