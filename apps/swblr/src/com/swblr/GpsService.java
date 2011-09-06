package com.swblr; 

import java.util.UUID;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GpsService extends Service{
	private MyLocationListener locationListener;
	private LocationManager locationManager;
	private Context context;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Service","Yes");
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
		locationManager =(LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		context=this;
		
		String provider = GpsUtils.getProvider(context,locationManager);
		locationManager.requestLocationUpdates(provider, 2000, 0, locationListener);
		
		
	    return START_STICKY;
	}
	
	public void onDestroy (){
		Log.d("Service","onDestroy");
		super.onDestroy();
		String uuid=getUuid();
		Log.d("GPS",locationListener.routeData);
		sendEmail(locationListener.routeData, uuid, "tppandey@gmail.com");
		locationManager.removeUpdates(locationListener);
		
	}
	public void sendEmail(String body, String subject, String address) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
	         
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
	    		new String[]{ address });
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
	    // emailIntent.putExtrta(android.content.Intent.EXTRA_STREAM, myImageStream);
	    Intent i=new Intent(Intent.createChooser(emailIntent, "Send mail..."));
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	    startActivity(i);
	    
	}
    
	private String getUuid() {
		String deviceId;
    	final TelephonyManager tm = (TelephonyManager) 
    		getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

		// Generate a uid for the phone
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(
	    		getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	
	    UUID deviceUuid = new UUID(androidId.hashCode(), 
	    		((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    deviceId = deviceUuid.toString();
	    Log.d("UID", deviceId);
	    
	    return deviceId;
	}

}
