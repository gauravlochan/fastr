package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.findme.SendSMS;
import in.beetroute.apps.traffic.R;

import com.google.android.maps.MapView;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author pradeep
 * This base class represents the main action bar for all views. It's recommended for all views to inherit from this class.
 * TO-DO: Figure out if this can be incorporated within BRMapActivity.
 *
 */
public class CommonMapView extends GDMapActivity {
	private MapView mapView;
	private static final int ENTER_DESTINATION_REQUEST_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 setActionBarContentView(R.layout.map);
	        
	        //Add a help button
	        addActionBarItem(Type.Help,R.id.action_bar_help);

	        // Add the direction button
	        addActionBarItem(Type.Export, R.id.action_bar_directions);
	        
	        // Add the route history button
	        // TODO: Enable this in a future release
	        // addActionBarItem(Type.List, R.id.action_bar_triplist);
	        
	        // Add the find me icon to the action bar
	        addActionBarItem(Type.LocateMyself, R.id.action_bar_findme);

	        mapView = (MapView) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
	        mapView.getController().setZoom(15);
	}

	 @Override
	    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

	        switch (item.getItemId()) {
	            case R.id.action_bar_directions:
	                 startActivityForResult(new Intent(this, EnterAddressActivity.class), 
	                		ENTER_DESTINATION_REQUEST_CODE);
	                break;
	                
	            case R.id.action_bar_triplist:
	                startActivity(new Intent(this, TripListActivity.class));
	                break;
	            
	            case R.id.action_bar_findme:
	            	startActivity(new Intent(this, SendSMS.class));
	            	break;
	            	
	            case R.id.action_bar_help:
	            	//startActivity(new Intent(this, HelpActivity.class));
	            	//TextView hu = (TextView)findViewById(R.id.textview);
	                //hu.setText("text");
	                
	                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	                builder.setMessage(
	                        R.string.helpString)
	                        .setCancelable(false)
	                        .setTitle(new String("Help"))
	                        .setPositiveButton("OK",
	                                new DialogInterface.OnClickListener() {
	                                    public void onClick(
	                                            @SuppressWarnings("unused") final DialogInterface dialog,
	                                            @SuppressWarnings("unused") final int id) {
	                                    	
	                             
	                                    }
	                                });
	                final AlertDialog alert = builder.create();
	                alert.show();

	            default:
	                return super.onHandleActionBarItemClick(item, position);
	        }

	        return true;
	    }
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
