package in.fastr.apps.stuck;

import in.fastr.apps.common.UploadRecords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class StuckInTrafficActivity extends Activity {

    private LocationManager locationManager;
    private DbWrapper dbWrapper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbWrapper = new DbWrapper(this);
        dbWrapper.createDatabase();
    }

    /**
     * OnCLick Listener for the StuckInTraffic Button
     * Invoked from main.xml
     */
    public void recordGPS(View view){
      Intent intent = new Intent().setClass(this, TabPage.class);
      startActivity(intent);
      locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
      // TODO: Does it really need to create a new Listener each time?
      LocationListener mlocListener = new MyLocationListener();
      //TODO need to review getProvider() method
      String provider = getProvider(this, locationManager);
      locationManager.requestLocationUpdates( provider, 0, 0, mlocListener);
      
      UploadRecords.upload();
    }

    // TODO: Make this a regular class, not an inner class
    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            double longitude = loc.getLatitude();
            double latitude = loc.getLongitude();
            float speed = loc.getSpeed();
            Log.d(App.Name, "GPS Latitude" + Double.toString(latitude));
            Log.d(App.Name, "GPS Longitude" + Double.toString(longitude));

            StuckInTrafficActivity.this.locationManager.removeUpdates(this);

            /* Insert data to a Table */
            // TODO decide below which speed should the congestion point.
            // Currently setting in to 5
            if (speed < 5) {
                dbWrapper.insertPoint(latitude, longitude);
            }            
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

    }

    public static String getProvider(Context context, LocationManager locManager) {
        Context cntx = context;
        LocationManager locationManager = locManager;

        String provider = "";
        Criteria criteria = new Criteria();
        if (cntx.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setSpeedRequired(true);
            criteria.setBearingRequired(true);
        } else if (cntx
                .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, true);
        return provider;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(App.Name, "KILL");
        dbWrapper.logDatabase();
    }
   

}
