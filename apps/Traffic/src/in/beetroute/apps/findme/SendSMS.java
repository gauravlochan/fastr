package in.beetroute.apps.findme;

import greendroid.app.GDActivity;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.location.LocationHelper;
import in.beetroute.apps.traffic.location.OneTimeGpsLocationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

/**
 * @author pradeep Main activity class to send the GPS co-ordinates to the
 *         selected contact. A contact list is displayed with the names and Ids.
 *         Once a user taps on a contact, the GPS co-ordinates are sent to that
 *         contact as an SMS.
 */

public class SendSMS extends GDActivity {
    private static final int PICK_CONTACT = 1;
    private static final int ENABLE_GPS = 0;
    
    private static final String TAG = Global.COMPANY;
    private static final String SENT = "SMS_SENT";
    
    private ProgressDialog progressdialog;
    private BroadcastReceiver smsReceiver;
    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.findme);
        
        initBroadcastReceiver();

        if (!LocationHelper.doesDeviceHaveGps(this)) {
            // TODO: Need to warn user that device doesn't have GPS at all.  For now, go back
            Logger.warn(TAG,  "Device doesn't have GPS.  Can't send FindMe SMS");
        }

        if (!LocationHelper.isGpsEnabled(this)) {
            // Pop up a dialog asking the user if they want to enable GPS
            buildAlertMessageNoGps();
        } else {
            // Everything is good. Kick off a task to get the current location
            new LocationLookupTask().execute((Void)null);
        }
    }
    
    @Override
   	protected void onStart() {
   		// TODO Auto-generated method stub
   		super.onStart();
   		FlurryAgent.onStartSession(this, "3K4UUTXNPWWT1GPGHC6L");
   	}


   	@Override
   	protected void onStop() {
   		// TODO Auto-generated method stub
   		super.onStop();
   		FlurryAgent.onEndSession(this);
   	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    
    /**
     * Initialize the broadcast receiver to figure out when the SMS was sent
     * successfully
     */
    private void initBroadcastReceiver() {
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

                    String messageToSend = GeoSMS.constructSMS(this, location);
                    sendSms(phoneNumber, messageToSend);
                }
            } else {
                Logger.debug(TAG, "User didn't pick a contact.  End this activity");
                finish();
            }
            break;
            
        case ENABLE_GPS:
            // Doublecheck that the user actually enabled the GPS
            if (LocationHelper.isGpsEnabled(this)) {
                // Since we've *just* enabled the GPS, wait to get a good location
                new LocationLookupTask().execute((Void) null);
            } else {
                Logger.debug(TAG, "User didn't enable GPS.  End this activity");
                finish();
            }
            break;

        default:
            // Just checking to see if the enableGPS activity is coming back here
            Logger.debug(TAG, "Ignore result from activity with request code " + requestCode);
            break;
        }
    }

    
    /**
     * Have got the location and contact. Now just fire off the SMS
     * 
     * @param phoneNumber
     * @param textMessage
     */
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


    /**
     * If the GPS is disabled, ask the user to enable it.
     */
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
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, ENABLE_GPS);
    }
    
 
    /**
     * Task to get an accurate location.  Assumes that the GPS is enabled.
     * 
     * Once the result comes in, handles it
     */
    class LocationLookupTask extends AsyncTask<Void, Void, Location> {
        private ProgressDialog progressDialog;
        private OneTimeGpsLocationListener listener;

        @Override
        protected void onPreExecute() {
            LocationManager locationManager = (LocationManager) SendSMS.this.
                    getSystemService(Context.LOCATION_SERVICE);
            listener = new OneTimeGpsLocationListener(locationManager);

            this.progressDialog = ProgressDialog.show(
                    SendSMS.this,
                    "Please wait...", // title
                    "Getting the current location", // message
                    true // indeterminate
                    );
        }

        @Override
        protected Location doInBackground(Void... params) {
            Logger.debug(TAG, "Starting to get a good location");
            Location location = listener.waitForLocation();
            if (location != null) {
                return location;
            }
            
            // In case we didn't get a fresh location update in the time limit,
            // try and fall back to the getLastKnownLocation (which could also be null BTW)
            return LocationHelper.getLastGpsLocation(SendSMS.this);
        }

        @Override
        protected void onPostExecute(Location gotLocation) {
            // TODO: Temporary fix for the "View not attached to window manager" issue
            try {
                this.progressDialog.cancel();
            } catch (IllegalArgumentException e) {
                // The original activity has been killed, don't crash the app
                return;
            }
            
            if (gotLocation == null) {
                //
                // TODO: Tell the user we couldn't get a fix in time
                //
                SendSMS.this.finish();
            } else {
                location = gotLocation;
                // Invoke the contacts intent to move along
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        }
    }
    

}
