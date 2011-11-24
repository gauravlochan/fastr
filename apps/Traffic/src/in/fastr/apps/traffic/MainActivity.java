package in.fastr.apps.traffic;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

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

        // Add the direction button
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(R.drawable.ic_title_export)
                .setContentDescription(R.string.gd_export), 
                R.id.action_bar_export);

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
            case R.id.action_bar_export:
                Toast.makeText(this, R.string.clicked_on_direction, Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }

        return true;
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