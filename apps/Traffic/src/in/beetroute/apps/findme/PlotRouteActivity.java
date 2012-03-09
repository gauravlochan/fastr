package in.beetroute.apps.findme;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.EnterAddressActivity;
import in.beetroute.apps.traffic.activities.MainActivity;
import in.beetroute.apps.traffic.activities.RouteListActivity;
import android.content.Intent;
import android.os.Bundle;

public class PlotRouteActivity extends GDMapActivity {
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
        
        //Add a help screen
        addActionBarItem(Type.Help, R.id.action_bar_help);

        // Add the direction button
        addActionBarItem(Type.Export, R.id.action_bar_directions);
        
        // Add the route history button
        addActionBarItem(Type.List, R.id.action_bar_routelist);
        
        // Add the find me icon to the action bar
        addActionBarItem(Type.LocateMyself, R.id.action_bar_findme);  
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
             
         case R.id.action_bar_routelist:
             startActivity(new Intent(this, RouteListActivity.class));
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		startActivity( new Intent(this,MainActivity.class));
	}

}
