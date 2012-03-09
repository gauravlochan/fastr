package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.BRMapActivity;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PlotRouteActivity extends BRMapActivity {
    private static final String TAG = Global.COMPANY;
    
    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG, "Creating PlotRouteActivity");
        super.onCreate(savedInstanceState);
        
        setActionBarContentView(R.layout.showroute);
        mapView = (MapView) findViewById(R.id.mapview);
        
        resetMapOverlays();

        // Get the destination address from the SMS
        Bundle extras = getIntent().getExtras();
        MapPoint destination = (MapPoint) extras.getSerializable(AppGlobal.LOCATION_FROM_SMS_KEY);

        // Get the route from here to the destination
        GeoPoint sgPoint = getLastKnownLocation();
        SimpleGeoPoint source = new SimpleGeoPoint(sgPoint);
        
        getAndDrawRoutes(source, destination);
    }

}
