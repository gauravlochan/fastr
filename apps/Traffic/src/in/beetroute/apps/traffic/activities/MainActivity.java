package in.beetroute.apps.traffic.activities;

import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.QuickActionWidget;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.findme.SendSMS;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.Preferences;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.google.directions.GoogleDirectionsService;
import in.beetroute.apps.traffic.location.LocationService;
import in.beetroute.apps.traffic.services.DirectionsService;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MainActivity extends BRMapActivity {
    private static final String TAG = Global.COMPANY;
    
    // Trying out the quickaction for findme icon
    private QuickActionWidget quickAction;

	// Define a request code for the Enter address activity
	private static final int ENTER_DESTINATION_REQUEST_CODE = 100;
	
    // These are preserved across recreations (OnSaveInstanceState)
    private MapPoint destination;
    private MapPoint source;
    private Timer timer;
    private RouteTimerTask timerTask = null;

    private static final String SAVE_SOURCE = "SaveSource";
    private static final String SAVE_DESTINATION = "SaveDestination";	
	
	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG, "Creating MainActivity");

		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.map);

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
		
		resetMapOverlays();
		
		// Try to center around current location
        GeoPoint geoPoint = getLastKnownLocation();
        mapView.getController().setCenter(geoPoint);
		Toast.makeText(this, "You are here", Toast.LENGTH_SHORT).show();
		
		// Setup the installation ID
		Preferences.getInstallationId(this);
		
        // Start the service in case it is already not running
        Intent i=new Intent(this, LocationService.class);
        startService(i);

        timer = new Timer();
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
        // If a route has been plotted, add a location listener for that route
	    if (destination != null) {
	        scheduleTimerTask();
	    }
	}
	
	
	@Override
	public void onPause() {
	    super.onPause();
	    // If a route has been plotted, remove the location listener for that route
	    if (destination != null) {
	        unscheduleTimerTask();
	    }
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
                //quickAction = new QuickActionGrid(this);
                //quickAction.addQuickAction(new QuickAction(getApplicationContext(),R.drawable.gd_action_bar_locate_myself, new String("facebook")));
                //quickAction.show(mapView);
            	startActivity(new Intent(this, SendSMS.class));
            	break;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }

        return true;
    }

    
    /**
     * The child activities that would call back here are:
     * - Enter destination address/place - will return with the destination point
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	if (resultCode == RESULT_OK && requestCode == ENTER_DESTINATION_REQUEST_CODE) {
            Logger.info(TAG, "resultCode: " + resultCode );
            
            if (data.hasExtra(AppGlobal.destPoint)) {
                // Clear any previous route
                resetMapOverlays();

                // Plot the new route
                destination = (MapPoint) data.getExtras().getSerializable(AppGlobal.destPoint);
            	if (data.hasExtra(AppGlobal.sourcePoint)) {
                    // If the user specified a source, plot that route
            	    source = (MapPoint) data.getExtras().getSerializable(AppGlobal.sourcePoint);
            	} else{
            	    // Plot the route from the current location
	            	SimpleGeoPoint location = new SimpleGeoPoint(getLastKnownLocation());
	            	source = new MapPoint("Current Location", "", location);
            	}
            	
            	getAndDrawRoutes(source, destination);
            	
                // Call BTIS asynchronously to get congestion points and plot them on the map
                // TODO: Eventually pass in the route that we care about
                new GetCongestionTask(this, mapView).execute(null);
                
            	// Call the server to send this route (happens in an async task)
//            	ServerClient serverclient = new ServerClient();
//            	serverclient.sendRoute(route);
//            	
            } else {
            	// TODO: Add support for destination addresses
            	Logger.error(TAG, "Did not find point of interest in intent");
            }
    	}
    }

    
    @Override 
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(SAVE_DESTINATION, destination);
        bundle.putSerializable(SAVE_SOURCE, source);
    }
    

    @Override 
    protected void onRestoreInstanceState(Bundle bundle) {
        // TODO: This is time consuming AND can lead to multiple redraws
        // Just save all the relevant data away and reuse it.
        Object _source = bundle.getSerializable(SAVE_SOURCE);
        Object _destination = bundle.getSerializable(SAVE_DESTINATION);
        
        if ((_source!=null) && (_destination!=null)) {
            source = (MapPoint) _source;
            destination = (MapPoint) _destination;
            getAndDrawRoutes(source, destination);
        }
    }

    @Override
    protected void resetMapOverlays() {
        super.resetMapOverlays();
        findViewById(R.id.transparent_panel).setVisibility(View.INVISIBLE);
    }

    // TODO: This is being called in the UI thread.  Fix this?
    @Override
    protected Route getAndDrawRoutes(MapPoint source, MapPoint dest) {
        Route route = super.getAndDrawRoutes(source, dest);

        // Now make the panel visible and update it
        findViewById(R.id.transparent_panel).setVisibility(View.VISIBLE);
        runOnUiThread(new UpdateTimeRunnable(route));
        return route;
    }


    
    private void scheduleTimerTask() {
        timerTask = new RouteTimerTask();
        int cancelled = timer.purge();
        // First execution after 10 seconds, subsequent after 3 minutes
        timer.schedule(timerTask, 10*1000, 180*1000);
        Logger.debug(TAG, "Scheduled timerTask");
    }
    
    private void unscheduleTimerTask() {
        timerTask.cancel();
        timerTask = null;
        Logger.debug(TAG, "Unscheduled timerTask");
    }
    
    // http://stackoverflow.com/questions/7010951/error-updating-textview-from-timertasks-run-method
    private class UpdateTimeRunnable implements Runnable {
        Route route;
        public UpdateTimeRunnable(Route route) {
            this.route = route;
        }
        
        @Override
        public void run() {
            String text = "Remaining: " + route.drivingDistanceMeters/1000 + "km, " 
                    + route.estimatedTimeSeconds/60+ "min.";
            Logger.debug(TAG, "Updated Route " + text);

            TextView hud = (TextView)findViewById(R.id.textview);
            hud.setText("Remaining: " + route.drivingDistanceMeters/1000 + "km, " 
                + route.estimatedTimeSeconds/60+ "min.");
        }
    }
    
    /**
     * This timerTask is responsible for updating the Remaining time/distance
     * display on the map
     * 
     * @author gauravlochan
     */
    private class RouteTimerTask extends TimerTask {

        @Override
        public void run() {
            // TODO: get current location from DB instead
            SimpleGeoPoint currentLocation = new SimpleGeoPoint(getLastKnownLocation());
            Logger.debug(TAG, "Timer invoked, found location as "+currentLocation);
            
            // Get route
            DirectionsService dir = new GoogleDirectionsService();
            Route route = dir.getFirstRoute(currentLocation, destination.getSimpleGeoPoint());

            // Update textView
            runOnUiThread(new UpdateTimeRunnable(route));
        }
        
    }

}
