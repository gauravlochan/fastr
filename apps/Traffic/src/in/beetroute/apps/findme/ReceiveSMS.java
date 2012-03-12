package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class ReceiveSMS extends BroadcastReceiver {
    private static final String TAG = Global.COMPANY;

	@Override
	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 * In the onReceive method, we pull the message sent
	 */
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        System.out.println("In the SMS Receive body");
        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];
        for (int i = 0; i < messages.length; ++i) {
            sms[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
        }

        for (int i = 0; i < messages.length; ++i) {
            String smsSource = sms[i].getOriginatingAddress();
            String message = sms[i].getMessageBody().toString();
            if (GeoSMS.matchMessage(message)) {
                // Extract the location to use for plotting on the map
                Location location = GeoSMS.extractLocation(message);
                if (location == null) {
                    Logger.warn(TAG, "Unable to extract location from SMS");
                } else {
                    MapPoint mapPoint = new MapPoint("Location of " + smsSource,
                            message, location.getLatitude(), 
                            location.getLongitude());
    
                    Intent showDialogIntent = new Intent(context,
                            ConfirmPlotRoute.class);
                    showDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    showDialogIntent.putExtra(AppGlobal.LOCATION_FROM_SMS_KEY, mapPoint);
    
                    context.startActivity(showDialogIntent);
                }
            }
        }
    }

}