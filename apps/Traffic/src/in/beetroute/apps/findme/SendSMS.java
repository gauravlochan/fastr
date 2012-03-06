package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
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
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author pradeep
 * Main activity class to send the GPS co-ordinates to the selected contact. A contact list is displayed with the names and Ids.
 * Once a user taps on a contact, the GPS co-ordinates are sent to that contact as an SMS.
 */
public class SendSMS extends ListActivity {
	private static final int PICK_CONTACT = 1;
	private ArrayList<HashMap<String, String>> contactDetails;
	private ArrayList<String> contactNames;
	private static final String TAG = Global.COMPANY;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(in.beetroute.apps.traffic.R.layout.findme);
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
		/*
		Cursor cursor = getContacts();
		contactNames = new ArrayList<String>();
		contactDetails = new ArrayList<HashMap<String, String>>();
		if (cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				//String phoneNumber = getPhoneNumber(id);
				String phoneNumber = "";
				String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contact = displayName + ":" + id;
				//String contact = displayName;
				HashMap<String,String> idMap = new HashMap<String, String>();
				idMap.put(displayName, id);
				//idMap.put("name", displayName);
				contactNames.add(contact);
				contactDetails.add(idMap);
			}

		}
		
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,contactNames);
		setListAdapter(adapter);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				displayItems(position);
				
				Iterator iterator = contactDetails.iterator();
				while(iterator.hasNext()) {
					HashMap<String, String> map =(HashMap<String, String>)iterator.next();
					System.out.println(map);
				}
				
			}
		});
		*/
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case PICK_CONTACT:
			if(resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if(c.moveToFirst()) {
					String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
					Logger.info(TAG, id);
					String phoneNumber = getPhoneNumber(id);
					String gpsPosition = getGpsData(getApplicationContext());
					String messageToSend = gpsPosition.concat(",latLng");

					if(!gpsPosition.equals("NO GPS")) {
						sendSms(phoneNumber, messageToSend);
					}
					
				}
			}
		}
	}



	private Cursor getContacts(){
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] {ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME};
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + "='" + ("1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		Cursor c = null;
		try {
			c = managedQuery(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	private void displayItems(int position) {
		String displayName = this.getListAdapter().getItem(position).toString();
		String[] nameArray = displayName.split(":");
		String id = nameArray[1];
		String phoneNumber = getPhoneNumber(id);
		String gpsPosition = getGpsData(getApplicationContext());
		String messageToSend = gpsPosition.concat(",latLng");
		if(!gpsPosition.equals("NO GPS")) {
			sendSms(phoneNumber, messageToSend);
		}
	}

	private void sendSms(String phoneNumber, String textMessage) {
		String SENT="SMS_SENT";
		//System.out.println(idMap.get(displayName));
		try {
			PendingIntent sentPi = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
			registerReceiver(new BroadcastReceiver(){

				@Override
				public void onReceive(Context arg0, Intent arg1) {
					// TODO Auto-generated method stub
					switch(getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(getBaseContext(), "Your location information has been sent", Toast.LENGTH_LONG).show();
						break;
					}
					
				}
				
			},new IntentFilter(SENT));
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(phoneNumber, null, textMessage, sentPi, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getPhoneNumber(String displayName) {
		String phoneNumber = new String("");
		ContentResolver resolver = getContentResolver();
		try {
			Cursor cur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.
					CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] {displayName}, null);
			while(cur.moveToNext()) {
				phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phoneNumber;
	}

	public String getGpsData(Context context) {
		String latlon = "";
		try {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = locationManager.getAllProviders();
			// Add code to check if GPS is on and if it's not, provide a popup with the message 
			// "Can we turn on and turn off the GPS just to get your location"

			if(!providers.isEmpty()) {
				if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) ){
					Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					latlon = new String(String.valueOf(latitude) + ":" + String.valueOf(longitude));
					return latlon;
				} else {
					buildAlertMessageNoGps();
					latlon = "NO GPS";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latlon;
	}

	private void buildAlertMessageNoGps() {
		try {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
					launchGPSOptions(); 
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
					dialog.cancel();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void launchGPSOptions() {
		final ComponentName toLaunch = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");
		final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(toLaunch);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivityForResult(intent, 0);
	}

}
