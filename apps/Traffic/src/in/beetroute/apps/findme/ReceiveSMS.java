package in.beetroute.apps.findme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class ReceiveSMS extends BroadcastReceiver {
	@Override
	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 * In the onReceive method, we pull the message sent
	 */
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		System.out.println("In the SMS Receive body");
		Object[] messages = (Object[])bundle.get("pdus");
		SmsMessage[] sms = new SmsMessage[messages.length];
		for(int i=0;i<messages.length;++i){
			sms[i] = SmsMessage.createFromPdu((byte[])messages[i]);
		}
		for(int i=0;i<messages.length;++i){
			String address = sms[i].getOriginatingAddress();
			String message = sms[i].getMessageBody().toString();
			if(message.contains("latLng")) {
				String[] msgParts = message.split(",");
				String messageToSend = msgParts[0];
				System.out.println(messageToSend);
				Intent showDialogIntent = new Intent(context, ShowDialog.class);
				showDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Intent showRouteMapIntent = new Intent(context, PlotRouteMap.class);
				showRouteMapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				showRouteMapIntent.putExtra("address", address);
				showRouteMapIntent.putExtra("latlon", messageToSend);
				showDialogIntent.putExtra("address", address);
				showDialogIntent.putExtra("latlon", messageToSend);
				context.startActivity(showDialogIntent);
				
				//context.startActivity(showRouteMapIntent);
			}
		}
	}


}