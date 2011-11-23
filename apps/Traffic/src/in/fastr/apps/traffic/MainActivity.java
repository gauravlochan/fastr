package in.fastr.apps.traffic;

import greendroid.app.GDMapActivity;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends GDMapActivity {
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.map);

		//setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		LocationRetriever retriever = new LocationRetriever();
		Location location = retriever.getLastKnownLocation(locationManager);
		GeoPoint geoPoint = LocationHelper.locationToGeoPoint(location);

		mapView.getController().setCenter(geoPoint);
		mapView.getController().setZoom(15);

		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		// listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
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