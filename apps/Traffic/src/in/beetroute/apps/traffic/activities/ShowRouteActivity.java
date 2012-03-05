package in.beetroute.apps.traffic.activities;

import greendroid.app.GDMapActivity;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.R;
import android.os.Bundle;

public class ShowRouteActivity extends GDMapActivity {
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
            
            // TODO: Kick off task to get the route
        }
    }

}
