package in.beetroute.apps.findme;

import greendroid.app.GDActivity;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.R;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * @author pradeep Main activity class to send the GPS co-ordinates to the
 *         selected contact. A contact list is displayed with the names and Ids.
 *         Once a user taps on a contact, the GPS co-ordinates are sent to that
 *         contact as an SMS.
 */

public class SendSMS extends GDActivity {
    private static final int PICK_CONTACT = 1;
    private static final int GPS_POSITION = 0;
    private static final String TAG = Global.COMPANY;
    private static final String SENT = "SMS_SENT";
    private ProgressDialog progressdialog;
    private BroadcastReceiver smsReceiver;
    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setActionBarContentView(R.layout.findme);
    			
        super.onCreate(savedInstanceState);
        initBroadcastReceiver();
        location = getGpsData(this);
    	Intent intent = new Intent(Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI);
    	if (location != null) {
    		startActivityForResult(intent, PICK_CONTACT);
    	}
        
    }
    
    
    /**
     * Separating the broadcast receiver out of the sendSMS method. This has to be called by the onCreate method.
     */
    private void initBroadcastReceiver() {
        //Initialize the broadcast receiver;
    	
         try {
        	 smsReceiver = new BroadcastReceiver() {
         		public void onReceive(Context arg0, Intent arg1) {
                     switch (getResultCode()) {
                     case Activity.RESULT_OK:
                    	 if(progressdialog != null) {
                    		 progressdialog.dismiss();
                    		 Toast.makeText(getBaseContext(),
                                 "Your location information has been sent",
                                 Toast.LENGTH_SHORT).show();
                    		 SendSMS.this.finish();
                    	 }
                         break;
                     }

         		}
        	 };
             registerReceiver(smsReceiver, new IntentFilter(SENT));
            } catch (Exception e) {
            	e.printStackTrace();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case PICK_CONTACT:
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    Logger.info(TAG, id);
                    String phoneNumber = getPhoneNumber(id);
                   // Location location = getGpsData(this);
                    
                    // TODO: If location isn't accurate enough, warn the user

                    if (location != null) {
                        String messageToSend = GeoSMS.constructSMS(this, location);
                        sendSms(phoneNumber, messageToSend);
                        //this.finish();
                    } else {
                        Logger.warn(TAG, "Can't send SMS since location wasn't found");
                    }
                }
            } else {
                Logger.debug(TAG, "User didn't pick a contact.  End this activity");
                SendSMS.this.finish();
            }
            break;        
        case GPS_POSITION:
        	System.out.println(resultCode);
        	//Uri gpsData = data.getData();
        	//System.out.println(gpsData.toString());
        	if(resultCode == Activity.RESULT_CANCELED) {
        		SendSMS.this.finish();
        	}
        default:
            // Just checking to see if the enableGPS activity is coming back here
            Logger.debug(TAG, "Ignore result from activity with request code " + requestCode);
            break;
        }
    }

    private void sendSms(String phoneNumber, String textMessage) {
        String SENT = "SMS_SENT";
        try {
            PendingIntent sentPi = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, textMessage, sentPi, null);
            progressdialog = ProgressDialog.show(SendSMS.this, "", "Sending location information");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPhoneNumber(String displayName) {
        String phoneNumber = new String("");
        ContentResolver resolver = getContentResolver();
        try {
            Cursor cur = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[] { displayName }, null);
            while (cur.moveToNext()) {
                phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

    // TODO: Use LocationHelper code instead
    public Location getGpsData(Context context) {
    	Location location = null;
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getAllProviders();
            // Add code to check if GPS is on and if it's not, provide a popup
            // with the message
            // "Can we turn on and turn off the GPS just to get your location"

            if (!providers.isEmpty()) {
                if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    float accuracy = location.getAccuracy();
                } else {
                    buildAlertMessageNoGps();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private void buildAlertMessageNoGps() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(
                    "Yout GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(true)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        @SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                                    launchGPSOptions();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                    SendSMS.this.finish();
                                    
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchGPSOptions() {
        final ComponentName toLaunch = new ComponentName(
                "com.android.settings", "com.android.settings.SecuritySettings");
        final Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, GPS_POSITION);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 * Overridden onDestroy to call unregisterReceiver for the previously registered broadcastreceiver for receiving SMS messages.
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}

}
