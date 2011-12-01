package in.fastr.apps.traffic.activities;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import in.fastr.apps.traffic.AppGlobal;
import in.fastr.apps.traffic.R;
import in.fastr.apps.traffic.location.LocationHelper;
import in.fastr.apps.traffic.location.LocationRetriever;
import in.fastr.apps.traffic.services.PointOfInterest;
import in.fastr.library.Global;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends GDMapActivity {
	// Define a request code for the destination activity
	private static final int ENTER_DESTINATION_REQUEST_CODE = 100;
	
	MapView mapView;
	List<Overlay> mapOverlays;

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

		// Center the map
		GeoPoint geoPoint = getCurrentLocation();
		mapView.getController().setCenter(geoPoint);
		mapView.getController().setZoom(15);

		// Add overlays
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		// listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
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

            
            if (data.hasExtra(AppGlobal.destPointOfInterest)) {
            	PointOfInterest point = (PointOfInterest) 
            			data.getExtras().getSerializable(AppGlobal.destPointOfInterest);
            	drawPointOfInterest(point);            		 
            } else {
            	Log.e(Global.Company, "Did not find point of interest in intent");
            	// ERROR
            }
    	}
    }
    
    private void drawPointOfInterest(PointOfInterest point) {
    	Drawable drawable = this.getResources().getDrawable(R.drawable.gd_map_pin_pin);
        MapItemOverlay itemizedOverlay = new MapItemOverlay(drawable, this);

        OverlayItem overlayitem = new OverlayItem(point.getGeoPoint(), point.getName(), point.getDescription());
        itemizedOverlay.addOverlay(overlayitem);
        
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.add(itemizedOverlay);
        
        mapView.getController().animateTo(point.getGeoPoint());
    }
 
    
    
	private GeoPoint getCurrentLocation() {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		LocationRetriever retriever = new LocationRetriever();
		Location location = retriever.getLastKnownLocation(locationManager);
		return LocationHelper.locationToGeoPoint(location);
	}

	class MapOverlay extends Overlay {
		@Override
        public boolean onTouchEvent(MotionEvent e, MapView mapView) 
        {   
            if (e.getAction() == 1) {                
                GeoPoint p = mapView.getProjection().fromPixels(
                    (int) e.getX(),
                    (int) e.getY());
                // MainActivity.this.startActivityForResult(intent, requestCode);
            }                            
            return false;
        }
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_S) {
//			map.setSatellite(!map.isSatellite());
//			return (true);
//		} else if (keyCode == KeyEvent.KEYCODE_Z) {
//			map.displayZoomControls(true);
//			return (true);
//		}
//
//		return (super.onKeyDown(keyCode, event));
//	}

}