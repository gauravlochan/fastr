package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.ServiceProviders;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.BRMapActivity;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PlotRouteActivity extends BRMapActivity {
    private static final String TAG = Global.COMPANY;
    
    private MapPoint _destination;
    private SimpleGeoPoint _source;

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
        String latLongString = extras.getString("latlon");
        String[] destination = latLongString.split(":");
        double latitude = Double.valueOf(destination[0]);
        double longitude = Double.valueOf(destination[1]);
        
        
        _destination = new MapPoint(ServiceProviders.FINDME, "Your friend", 
                "This is the destination location that your friend sent you");
        _destination.setLocation(latitude, longitude);
        
        // Get the route from here to the destination
        GeoPoint source = getLastKnownLocation();
        _source = new SimpleGeoPoint(source);
        
        getAndDrawRoutes(_source, _destination);
    }

}
