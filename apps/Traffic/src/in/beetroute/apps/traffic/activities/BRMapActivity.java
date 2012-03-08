package in.beetroute.apps.traffic.activities;

import greendroid.app.GDMapActivity;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.Route;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

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
    
    protected MapView mapView;
    protected MyLocationOverlay myLocationOverlay;

    /**
     * Remove all the overlays and add a single 'MyLocationOverlay'
     * 
     * @return GeoPoint obtained from myLocationOverlay.  Can be null.
     */
    protected void resetMapOverlays() {
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
    
    
    protected void drawMultipleRoutes(List<Route> routes) {
        // TODO: 3 for now since google returns only 3 routes
        int colors[] = {Color.GREEN, Color.CYAN, Color.GRAY};
        
        for (int i = 0; i< routes.size(); i++) {
            Route route = routes.get(i);
            drawRoute(route, colors[i]);
        }
    }

    
    protected void drawRoute(Route r, int color) {
        MapRouteOverlay mapOverlay = new MapRouteOverlay(r, mapView, color);
        
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(0, mapOverlay);
        mapView.invalidate();
    }
    



}
