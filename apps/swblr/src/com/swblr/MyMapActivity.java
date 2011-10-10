package com.swblr;

import java.util.List;
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

public class MyMapActivity extends MapActivity {
  MapView mapView;
  List<Overlay> mapOverlays;
  Drawable drawable;
  MapItemOverlay itemizedOverlay;

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
    
    mapOverlays = mapView.getOverlays();
    drawable = this.getResources().getDrawable(R.drawable.beetle);
    itemizedOverlay = new MapItemOverlay(drawable, this);
    
    // TODO: Get location from GpsService
    GpsClient client = new GpsClient(this);
    Location location = client.getLastLocation();
    GeoPoint point;
    if (location != null) {
      point = new GeoPoint((int) (location.getLatitude() * 1E6),
          (int) (location.getLatitude() * 1E6));
    } else {
      point = new GeoPoint(17385812,78480667);
    }
    OverlayItem overlayitem = new OverlayItem(point, "Me",
        "Here I am!");
    
    itemizedOverlay.addOverlay(overlayitem);
    mapOverlays.add(itemizedOverlay);
  }
  
}