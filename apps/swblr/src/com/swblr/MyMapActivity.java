package com.swblr;

  import java.util.List;
  import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
  import android.os.Bundle;
  import com.google.android.maps.GeoPoint;
  import com.google.android.maps.MapActivity;
  import com.google.android.maps.MapView;
  import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

  public class MyMapActivity extends MapActivity
  {
    private double mLongitude = 0;
    private double mLatitude = 0;
    private MyLocationListener locationListener = new MyLocationListener();

    @Override
     public void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.map);
      
         MapView mapView = (MapView) findViewById(R.id.mapview);
         mapView.setBuiltInZoomControls(true);
      
         List<Overlay> mapOverlays = mapView.getOverlays();
         Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
      
         MapItemOverlay itemizedoverlay = new MapItemOverlay(drawable,this);
         setCurrentGpsLocation(null);
         GeoPoint point = new GeoPoint((int)(mLongitude * 1E6), (int)(mLatitude * 1E6));
         OverlayItem overlayitem = new OverlayItem(point, "Me", "Hello! I am in Bangalore ");
//       GeoPoint point = new GeoPoint(17385812,78480667);
//         OverlayItem overlayitem = new OverlayItem(point, "Namashkaar!", "I'm in Hyderabad, India!");
            
         itemizedoverlay.addOverlay(overlayitem);
         mapOverlays.add(itemizedoverlay);
     }
     @Override
     protected boolean isRouteDisplayed()
     {
         return false;
     }
     
     /**
      * Sends a message to the update handler with either the current location or 
      *  the last known location. 
      * @param location is either null or the current location
      */
      private void setCurrentGpsLocation(Location location) {
         if (location == null) {
           LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
           mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 3000, 0, locationListener); // Every 30000 msecs 
           location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);       
         }
         try {
           mLongitude = location.getLongitude();
           mLatitude = location.getLatitude();
         } catch (NullPointerException e) {
           //Log.i(TAG, "Null pointer exception " + mLongitude + "," + mLatitude);
         }
      }
  }