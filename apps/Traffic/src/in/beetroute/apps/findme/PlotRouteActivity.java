package in.beetroute.apps.findme;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.BRMapActivity;
import android.os.Bundle;

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

        // Get the destination address from the SMS
        Bundle extras = getIntent().getExtras();
        String toAddress = extras.getString("latlon");

    }

}
