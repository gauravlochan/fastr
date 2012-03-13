package in.beetroute.apps.findme;

import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.BRMapActivity;
import in.beetroute.apps.traffic.activities.EnterAddressActivity;
import in.beetroute.apps.traffic.activities.MainActivity;
import in.beetroute.apps.traffic.activities.TripListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.maps.MapView;


public class PlotRouteActivity extends BRMapActivity {
    private static final String TAG = Global.COMPANY;
    private static final int ENTER_DESTINATION_REQUEST_CODE = 100;
    
    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG, "Creating PlotRouteActivity");
        super.onCreate(savedInstanceState);
        
        setActionBarContentView(R.layout.showroute);
        
        // Add a help screen
        // TODO: Bring this back
        // addActionBarItem(Type.Help, R.id.action_bar_help);

        // Add the direction button
        addActionBarItem(Type.Export, R.id.action_bar_directions);
        
        // Add the route history button
        // TODO: Bring this back
        // addActionBarItem(Type.List, R.id.action_bar_triplist);
        
        // Add the find me icon to the action bar
        addActionBarItem(Type.LocateMyself, R.id.action_bar_findme);  
        mapView = (MapView) findViewById(R.id.mapview);       
        resetMapOverlays();

        // Get the destination address from the SMS
        Bundle extras = getIntent().getExtras();
        MapPoint destination = (MapPoint) extras.getSerializable(AppGlobal.LOCATION_FROM_SMS_KEY);

        // Get the route from here to the destination
        SimpleGeoPoint location = new SimpleGeoPoint(getLastKnownLocation());
        MapPoint source = new MapPoint("Current Location", "", location);

        getAndDrawRoutes(source, destination);

    }

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		// TODO Auto-generated method stub
		 switch (item.getItemId()) {
         case R.id.action_bar_directions:
             // Testing: Log the DB here
             // LocationDbHelper dbHelper = new LocationDbHelper(this, null);
             // dbHelper.logDatabase();

             startActivityForResult(new Intent(this, EnterAddressActivity.class), 
             		ENTER_DESTINATION_REQUEST_CODE);
             break;
             
         case R.id.action_bar_triplist:
             startActivity(new Intent(this, TripListActivity.class));
             break;
         
         case R.id.action_bar_findme:
         	//quickAction = new QuickActionGrid(this);
         	//quickAction.addQuickAction(new QuickAction(getApplicationContext(),R.drawable.gd_action_bar_locate_myself, new String("facebook")));
         	//quickAction.show(mapView);
         	startActivity(new Intent(this,SendSMS.class));
         	break;
         	
         case R.id.action_bar_help:
         	
         	break;
         	
         default:
             return super.onHandleActionBarItemClick(item, position);
		 }
		 return true;
		
	}

}
