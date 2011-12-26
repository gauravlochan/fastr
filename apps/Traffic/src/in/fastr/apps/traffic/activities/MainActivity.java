package in.fastr.apps.traffic.activities;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import in.fastr.apps.traffic.AppGlobal;
import in.fastr.apps.traffic.MapPoint;
import in.fastr.apps.traffic.R;
import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.SimpleGeoPoint;
import in.fastr.apps.traffic.google.directions.GoogleDirectionsService;
import in.fastr.apps.traffic.location.LocationHelper;
import in.fastr.apps.traffic.location.LocationRetriever;
import in.fastr.apps.traffic.services.DirectionsService;
import in.fastr.library.Global;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends GDMapActivity {
	// Define a request code for the destination activity
	private static final int ENTER_DESTINATION_REQUEST_CODE = 100;
	
	MapView mapView;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.d(Global.Company, "Creating MainActivity");

		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.map);

        // Add the direction button
        addActionBarItem(Type.Export, R.id.action_bar_directions);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(15);
		
		GeoPoint geoPoint = resetMapOverlays();

		// Center the map
		if (geoPoint == null) {
			geoPoint = getLastKnownLocation();
		}
		
		// In some phones (e.g. HTC Wildfire) even getLastKnownLocation fails
		if (geoPoint != null) {
		    mapView.getController().setCenter(geoPoint);
			Toast.makeText(this, "You are here", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private GeoPoint resetMapOverlays() {
        // Add a 'MyLocationOverlay' to track the current location
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        
        return myLocationOverlay.getMyLocation();
	}
	
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

        switch (item.getItemId()) {
            case R.id.action_bar_directions:
                startActivityForResult(new Intent(this, EnterAddressActivity.class), 
                		ENTER_DESTINATION_REQUEST_CODE);
                break;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }

        return true;
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	if (resultCode == RESULT_OK && requestCode == ENTER_DESTINATION_REQUEST_CODE) {
            Log.i( Global.Company, "resultCode: " + resultCode );
            
            if (data.hasExtra(AppGlobal.destPoint)) {
                // reset the map
                resetMapOverlays();

                // Draw the point of interest
            	MapPoint point = (MapPoint) 
            			data.getExtras().getSerializable(AppGlobal.destPoint);
            	drawPointOfInterest(point);     
            	
            	// Get the route from here to the destination
            	GeoPoint sourcePoint = getLastKnownLocation();
            	GeoPoint destination = point.getGeoPoint();
            	DirectionsService dir = new GoogleDirectionsService();
            	List<Route> routes = dir.getRoutes(new SimpleGeoPoint(sourcePoint), 
            	        new SimpleGeoPoint(destination));
            	drawMultipleRoutes(routes);
            	
            	// Call BTIS asynchronously to get congestion points and plot them on the map
            	// TODO: Eventually pass in the route that we care about
            	new GetCongestionTask(this, mapView).execute(null);

            	// Call the server to send this route (happens in an async task)
//            	ServerClient serverclient = new ServerClient();
//            	serverclient.sendRoute(route);
//            	
            } else {
            	// TODO: Add support for destination addresses
            	Log.e(Global.Company, "Did not find point of interest in intent");
            }
    	}
    }

    /**
     * Draws a point of interest
     * 
     * @param point
     */
    private void drawPointOfInterest(MapPoint point) {
        OverlayItem overlayItem = new OverlayItem(point.getGeoPoint(), point.getName(), point.getDescription());
        drawSinglePoint(R.drawable.gd_map_pin_pin, overlayItem);
        
        mapView.getController().animateTo(point.getGeoPoint());
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
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		LocationRetriever retriever = new LocationRetriever();
		Location location = retriever.getLastKnownLocation(locationManager);
		if (location == null) {
			return null;
		}
		return LocationHelper.locationToGeoPoint(location);
	}

}