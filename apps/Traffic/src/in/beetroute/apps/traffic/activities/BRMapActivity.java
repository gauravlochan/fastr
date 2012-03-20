package in.beetroute.apps.traffic.activities;

import greendroid.app.GDMapActivity;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.google.directions.GoogleDirectionsService;
import in.beetroute.apps.traffic.location.LocationHelper;
import in.beetroute.apps.traffic.services.DirectionsService;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * This is a base class for BeetRoute MapActivity.
 * 
 * This provides common functionality that all our MapViews need
 * 
 * @author gauravlochan
 *
 */
public abstract class BRMapActivity extends GDMapActivity {
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, "3K4UUTXNPWWT1GPGHC6L");
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(this);
	}


	private static final String TAG = Global.COMPANY;
    
    /*
     * This needs to be set in the onCreate for each subclass.
     */
    protected MapView mapView;
    protected MyLocationOverlay myLocationOverlay;

    
    /**
     * Remove all the overlays and add a single 'MyLocationOverlay'
     * 
     * @return GeoPoint obtained from myLocationOverlay.  Can be null.
     */
    protected void resetMapOverlays() {
        mapView.getOverlays().clear();

        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
            myLocationOverlay = null;
        }
        
        // Add a 'MyLocationOverlay' to track the current location
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        
        // Center when we get a fix.  This might cause a change in behavior 
//        myLocationOverlay.runOnFirstFix(new Runnable() {
//            public void run() {
//                mapView.getController().animateTo(myLocationOverlay.getMyLocation());
//            }
//        });
       
        return;
    }

    
    /**
     * Draws a point of interest
     * 
     * @param point
     */
    protected void drawPointOfInterest(MapPoint point, boolean animateTo) {
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
    protected void drawSinglePoint(int drawableId, OverlayItem overlayItem) {
        Drawable drawable = this.getResources().getDrawable(R.drawable.gd_map_pin_pin);
        MapItemOverlay itemizedOverlay = new MapItemOverlay(drawable, this);
        itemizedOverlay.addOverlayItem(overlayItem);
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(itemizedOverlay);
        mapView.invalidate();
    }

    
    /**
     * Draws routes and returns the shortest route.   
     * 
     * @param source
     * @param dest
     * @return Shortest route. This can be null.
     */
    protected Route getAndDrawRoutes(MapPoint source, MapPoint dest) {
        drawPointOfInterest(source, false);
        drawPointOfInterest(dest, false);     

        DirectionsService dir = new GoogleDirectionsService();
        List<Route> routes = dir.getRoutes(source.getSimpleGeoPoint(), dest.getSimpleGeoPoint());
        drawMultipleRoutes(routes);
        
        if (routes.size()>0) {
            return routes.get(0);
        } else {
            return null;
        }
    }

    
    protected void drawMultipleRoutes(List<Route> routes) {
        // TODO: 3 for now since google returns only 3 routes
        int colors[] = {Color.GREEN, Color.CYAN, Color.GRAY};
        
        for (int i = 0; i< routes.size(); i++) {
            Route route = routes.get(i);
            drawRoute(route, colors[i]);
        }
    }

    
    protected void drawRoute(Route r, int color) {
        DisplayMetrics metrics = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        MapRouteOverlay mapOverlay = new MapRouteOverlay(r, mapView, color, 
                metrics.widthPixels, metrics.heightPixels);
        
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(0, mapOverlay);
        mapView.invalidate();
    }
    
    
    protected GeoPoint getLastKnownLocation() {
        GeoPoint geoPoint;
        
        // First try to get current location from the MyLocationOverlay widget
        if (myLocationOverlay != null) {
           geoPoint = myLocationOverlay.getMyLocation();
           if (geoPoint != null) {
               return geoPoint;
           }
        }
        
        // Else try to call into location manager directly
        Location location = LocationHelper.getBestLocation(this);
        if (location != null) {
            return LocationHelper.locationToGeoPoint(location);
        }

        // HACK: In some phones (e.g. HTC Wildfire) our code to get the location fails
        // Center to Ashok Nagar police station :-)
        Logger.warn(TAG, "Unable to get a location from the phone.  Use default");
        SimpleGeoPoint sgPoint = new SimpleGeoPoint(12.971669, 77.610314);
        return sgPoint.getGeoPoint();
    }




}
