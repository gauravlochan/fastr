package in.beetroute.apps.traffic.activities;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
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
import in.beetroute.apps.traffic.location.LocationHelper;
import in.beetroute.apps.traffic.location.LocationService;
import in.beetroute.apps.traffic.services.DirectionsService;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends GDMapActivity {
    private static final String TAG = Global.COMPANY;

	// Define a request code for the destination activity
	private static final int ENTER_DESTINATION_REQUEST_CODE = 100;
	
	MapView mapView;
	MyLocationOverlay myLocationOverlay;

	// These are all obtained as part of the activity, and are candidates to save
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
                startActivity(new Intent(this, RouteListActivity.class));
                break;
            
            case R.id.action_bar_findme:
            	startActivity(new Intent(this,SendSMS.class));
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
                resetMapOverlays();

                // The destination comes from the child activity
            	_destination = (MapPoint) data.getExtras().getSerializable(AppGlobal.destPoint);
            	
            	// Get the route from here to the destination
            	GeoPoint source = getLastKnownLocation();
            	_source = new SimpleGeoPoint(source);
            	
            	getAndDrawRoutes(_source, _destination);

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

    private void getAndDrawRoutes(SimpleGeoPoint source, MapPoint dest) {
        // TODO: Should draw the source with a marker too.  
        drawPointOfInterest(dest, false);     

        DirectionsService dir = new GoogleDirectionsService();
        List<Route> routes = dir.getRoutes(source, new SimpleGeoPoint(dest.getGeoPoint()));
        drawMultipleRoutes(routes);
        
        // Call BTIS asynchronously to get congestion points and plot them on the map
        // TODO: Eventually pass in the route that we care about
        new GetCongestionTask(this, mapView).execute(null);
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

    
    /**
     * Remove all the overlays and add a single 'MyLocationOverlay'
     * 
     * @return GeoPoint obtained from myLocationOverlay.  Can be null.
     */
    private void resetMapOverlays() {
        mapView.getOverlays().clear();

        // Add a 'MyLocationOverlay' to track the current location
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
       
        return;
    }
    
    /**
     * Draws a point of interest
     * 
     * @param point
     */
    private void drawPointOfInterest(MapPoint point, boolean animateTo) {
        OverlayItem overlayItem = new OverlayItem(point.getGeoPoint(), point.getName(), point.getDescription());
        drawSinglePoint(R.drawable.gd_map_pin_pin, overlayItem);
        
        if (animateTo) {
            mapView.getController().animateTo(point.getGeoPoint());
        }
		Toast.makeText(this, point.getName(), Toast.LENGTH_LONG).show();
    }
    
    /**
     * A generic method for drawing a point on the map
     * 
     * @param drawableId
     * @param overlayItem
     */
    private void drawSinglePoint(int drawableId, OverlayItem overlayItem) {
    	Drawable drawable = this.getResources().getDrawable(R.drawable.gd_map_pin_pin);
        MapItemOverlay itemizedOverlay = new MapItemOverlay(drawable, this);
        itemizedOverlay.addOverlayItem(overlayItem);
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(itemizedOverlay);
        mapView.invalidate();
    }
    
    
    private void drawMultipleRoutes(List<Route> routes) {
        // TODO: 3 for now since google returns only 3 routes
        int colors[] = {Color.GREEN, Color.CYAN, Color.GRAY};
        
        for (int i = 0; i< routes.size(); i++) {
            Route route = routes.get(i);
            drawRoute(route, colors[i]);
        }
    }

    
    private void drawRoute(Route r, int color) {
        MapRouteOverlay mapOverlay = new MapRouteOverlay(r, mapView, color);
        
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(0, mapOverlay);
        mapView.invalidate();
    }
    
    
 	private GeoPoint getLastKnownLocation() {
 	    GeoPoint geoPoint;
 	    
 	    // First try to get current location from the MyLocationOverlay widget
 	    if (myLocationOverlay != null) {
 	       geoPoint = myLocationOverlay.getMyLocation();
 	       if (geoPoint != null) {
 	           return geoPoint;
 	       }
 	    }
 	    
        // Else try to call into location manager directly
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = LocationHelper.getLastKnownLocation(locationManager);
		if (location != null) {
		    return LocationHelper.locationToGeoPoint(location);
		}
		
        // HACK: In some phones (e.g. HTC Wildfire) our code to get the location fails
	    // Center to Ashok Nagar police station :-)
        SimpleGeoPoint sgPoint = new SimpleGeoPoint(12.971669, 77.610314);
        return sgPoint.getGeoPoint();
	}

}