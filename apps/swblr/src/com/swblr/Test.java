package com.swblr;

import java.util.UUID;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Test extends Activity {
	private GlobalContext globals = new GlobalContext();
	private PissedButtonListener pissedButttonListner;
	String curState="Start";
	private Button enablegps;
//	private MyLocationListener locationListener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pissedButttonListner = new PissedButtonListener(globals);
        globals.context = this;
        globals.mainActivity = this;
//        globals.uuid = getUuid();

//        globals.locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        

        Log.d("onCreate", "yes");
        
    	globals.displayText = (TextView) findViewById(R.id.onTStart);
    	
	    globals.startButton = (Button) findViewById(R.id.onBStart);
	    globals.startButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
	    
	    globals.pissedButton=(Button) findViewById(R.id.pissed);
	    globals.pissedButton.setOnClickListener(pissedButttonListner);
	    globals.pissedButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
	    
	    enablegps=(Button) findViewById(R.id.enablegps);
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if (provider.equals("gps")) {
	    	enablegps.setText("GPS Is On");
	    }
	    enablegps.setOnClickListener(new View.OnClickListener() {
			
  			@Override
  			public void onClick(View v) {
  				// TODO Auto-generated method stub
  				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
  				startActivity(intent);				
  			}
	    });
	    
	    Button mapButton = (Button) findViewById(R.id.mapView);
	    mapButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(globals.context, MyMapActivity.class);
            startActivity(intent);
          }
      });
	    
    }

    @Override
    public void onStart() {
    	super.onStart();
    	Log.d("onStart", "yes");
    	
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
    } 
    
    @Override
    public void onSaveInstanceState (Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putString("sbutton", globals.startButton.getText().toString());
    	Log.d("OutState","yes");
    	curState=globals.startButton.getText().toString();
    }
    @Override
    public void onResume() {
    	super.onResume();
     	Log.d("onResume", curState);
     	globals.startButton.setText(curState);

    }

    public void selfService(View v){
    	String currentState = globals.startButton.getText().toString();
    	Log.d("onClick",currentState);
    	Intent i=new Intent(this, GpsService.class);
    	if (currentState.equals("Start")) {    			
    			startService(i);
	    		// User wants to start
	    		Log.d("onClick",currentState);
		 		globals.startButton.setText("Stop");
		 		globals.startButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
		 		globals.displayText.setText("Tracking your route \n");
		 		// Define a listener that responds to location updates
//		 		locationListener = new MyLocationListener();
		 		
				// Register the listener with the Location Manager to receive location updates
//				String provider = GpsUtils.getProvider(context,locationManager);
				
				//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); // MinTime in ms and MinDistance in metre 
//				globals.locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
 //   	    }

    	} else {
    		// User wants to stop
    		stopService(i);
    		globals.startButton.setText("Start");
    		globals.startButton.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
    		globals.displayText.setText("Destination Reached \n");
 //   		globals.locationManager.removeUpdates(this.locationListener);
 //   		sendEmail(locationListener.routeData, globals.uuid, globals.address);
    	}
    }
}