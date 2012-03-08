package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.db.TripDbHelper;
import in.beetroute.apps.traffic.trip.Trip;
import android.graphics.Color;
import android.os.Bundle;

public class PlotTripActivity extends BRMapActivity {
    private static final String TAG = Global.COMPANY;

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG, "Creating ShowRouteActivity");

        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.showroute);
        
        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            Integer tripId = extras.getInt(AppGlobal.TRIP_KEY);
            plotTrip(tripId);
            
        }
    }

    // TODO: Call this asynchronously, so as not to block the map load
    private void plotTrip(Integer tripId) {
        TripDbHelper tripDbHelper = new TripDbHelper(this, null);
        
        Trip trip = tripDbHelper.getTrip(tripId);
        
        Route route = getRoute(tripId);
        this.drawRoute(route, Color.DKGRAY);
        
    }
    
    private Route getRoute(Integer tripId) {
        
        // TODO Get a Route object
        return null;
    }
    
    

}
