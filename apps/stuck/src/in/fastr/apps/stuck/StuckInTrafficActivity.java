package in.fastr.apps.stuck;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class StuckInTrafficActivity extends Activity {

    private LocationManager locationManager;
    private final String TableName = "congestionPoints";
    private SQLiteDatabase myDB = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Create a Database. */
        try {
            myDB = this.openOrCreateDatabase("Traffix", MODE_PRIVATE, null);

            /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TableName
                    + " (Latitude Double, Longitude Double, Timestamp Double);");
        }

        catch (Exception e) {
            Log.d("Traffix", "DB error", e);
        }

        finally {
            if (myDB != null) {
                // myDB.close();
            }
        }

    }

    public void recordGPS(View view) {
        setContentView(R.layout.stuckscreen);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        // TODO need to review getProvider() method
        String provider = getProvider(this, locationManager);
        locationManager.requestLocationUpdates(provider, 0, 0, mlocListener);
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            double longitude = loc.getLatitude();
            double latitude = loc.getLongitude();
            float speed = loc.getSpeed();
            Log.d("GPS Latitude", Double.toString(latitude));
            Log.d("GPS Longitude", Double.toString(longitude));
            Date d = new Date();
            long epochtime = d.getTime();

            StuckInTrafficActivity.this.locationManager.removeUpdates(this);

            /* Insert data to a Table */
            if (speed < 5) {
                Log.d("trafficx", "entering");
                myDB = openOrCreateDatabase("Traffix", MODE_PRIVATE, null);
                myDB.execSQL("INSERT INTO " + TableName
                        + " (Latitude, Longitude, Timestamp)" + " VALUES ("
                        + latitude + ", " + longitude + ", " + epochtime + ");");
            }

            // TODO decide below which speed should the congestion point.
            // Currently setting in to 5

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

        Cursor c = myDB.rawQuery("SELECT * FROM " + TableName, null);

        int Column1 = c.getColumnIndex("Latitude");
        int Column2 = c.getColumnIndex("Longitude");

        c.moveToFirst();
        if (c != null) {
            // Loop through all Results
            do {
                Log.d("DB", "KILL");
                double lat = c.getDouble(Column1);
                double lon = c.getDouble(Column2);
                Log.d("DB", Double.toString(lat));
                Log.d("DB", Double.toString(lon));
            } while (c.moveToNext());
        }
    }
}
