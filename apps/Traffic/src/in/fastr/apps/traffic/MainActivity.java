package in.fastr.apps.traffic;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity {
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
    setContentView(R.layout.map);
    
    mapView = (MapView) findViewById(R.id.mapview);
    mapView.setBuiltInZoomControls(true);
    
    // Acquire a reference to the system Location Manager
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    LocationRetriever retriever = new LocationRetriever();
    Location location = retriever.getLastKnownLocation(locationManager);
    GeoPoint geoPoint = LocationHelper.locationToGeoPoint(location);
    
    mapView.getController().setCenter(geoPoint);
    mapView.getController().setZoom(15);
    
    //mapOverlays = mapView.getOverlays();
  }
  
  
  
}