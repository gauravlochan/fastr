package in.beetroute.apps.findme;

import greendroid.app.GDMapActivity;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.R;
import android.os.Bundle;

public class PlotRouteActivity extends GDMapActivity {
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
    }

}
