package in.beetroute.apps.traffic.activities;

import java.util.List;

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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends BRMapActivity {
    private static final String TAG = Global.COMPANY;
    
    //Trying out the quickaction for findme icon
    private QuickActionWidget quickAction;

	// Define a request code for the Enter address activity
	private static final int ENTER_DESTINATION_REQUEST_CODE = 100;
	
	// These are preserved across recreations (OnSaveInstanceState)
 	private MapPoint _destination;
	private SimpleGeoPoint _source;

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
        addActionBarItem(Type.List, R.id.action_bar_routelist);
        
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
	}
	
	
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

        switch (item.getItemId()) {
            case R.id.action_bar_directions:
                // Testing: Log the DB here
                // LocationDbHelper dbHelper = new LocationDbHelper(this, null);
                // dbHelper.logDatabase();

                startActivityForResult(new Intent(this, EnterAddressActivity.class), 
                		ENTER_DESTINATION_REQUEST_CODE);
                break;
                
            case R.id.action_bar_routelist:
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
                // reset the map
            	Log.v("logging:",AppGlobal.destPoint);
                resetMapOverlays();

                // The destination comes from the child activity
            	_destination = (MapPoint) data.getExtras().getSerializable(AppGlobal.destPoint);
            	
            	// Get the route from here to the destination
            	
            	if(data.hasExtra(AppGlobal.sourcePoint)){
            		MapPoint sourceMapPoint = (MapPoint) data.getExtras().getSerializable(AppGlobal.sourcePoint);
            		drawPointOfInterest(sourceMapPoint, false);
            		_source = sourceMapPoint.getSimpleGeoPoint();
            	} else{
	            	GeoPoint source = getLastKnownLocation();
	            	_source = new SimpleGeoPoint(source);
            	}
            	getAndDrawRoutes(_source, _destination);
            	
                // Call BTIS asynchronously to get congestion points and plot them on the map
                // TODO: Eventually pass in the route that we care about
                new GetCongestionTask(this, mapView).execute(null);
            	
            	// Acquire a reference to the system Location Manager
            	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            	// Define a listener that responds to location updates
            	LocationListener locationListener = new LocationListener() {
            	    public void onLocationChanged(Location location) {
            	      // Called when a new location is found by the network location provider.
            	      TextView hud = (TextView)findViewById(R.id.textview);
            	      findViewById(R.id.transparent_panel).setVisibility(0);
            	      SimpleGeoPoint currentLocation = new SimpleGeoPoint(location.getLongitude(),location.getLatitude());
            	      DirectionsService dir = new GoogleDirectionsService();
            	      List<Route> routeList = dir.getRoutes(currentLocation, _destination.getSimpleGeoPoint());
            	      if(routeList.size()!=0){
            	    	  hud.setText("Remaining: " + routeList.get(0).drivingDistanceMeters/1000 + "km, " + routeList.get(0).estimatedTimeSeconds/60+ "min.");
            	      }
            	    }

            	    public void onStatusChanged(String provider, int status, Bundle extras) {}

            	    public void onProviderEnabled(String provider) {}

            	    public void onProviderDisabled(String provider) {}
            	  };

            	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
            	
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
        bundle.putSerializable(SAVE_DESTINATION, _destination);
        bundle.putSerializable(SAVE_SOURCE, _source);
    }
    

    @Override 
    protected void onRestoreInstanceState(Bundle bundle) {
        // TODO: This is time consuming AND can lead to multiple redraws
        // Just save all the relevant data away and reuse it.
        Object source = bundle.getSerializable(SAVE_SOURCE);
        Object destination = bundle.getSerializable(SAVE_DESTINATION);
        
        if ((source!=null) && (destination!=null)) {
            _source = (SimpleGeoPoint) source;
            _destination = (MapPoint) destination;
            getAndDrawRoutes(_source, _destination);
        }
    }

}
