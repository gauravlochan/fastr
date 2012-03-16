package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.Route;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

// From http://code.google.com/p/j2memaprouteprovider/ which i found from
// http://stackoverflow.com/questions/2023669/j2me-android-blackberry-driving-directions-route-between-two-locations
//
// Also check out 
// http://stackoverflow.com/questions/2176397/drawing-a-line-path-on-google-maps
// http://stackoverflow.com/questions/1612533/android-drivingdirections-removed-since-api-1-0-how-to-do-it-in-1-5-1-6
public class MapRouteOverlay extends Overlay {
    public final static String TAG = Global.COMPANY;
    
	List<SimpleGeoPoint> sgPoints;
	int color;
	int maxX;
	int maxY;

	public MapRouteOverlay(Route route, MapView mv, int color, int maxX, int maxY) {
		sgPoints = route.getPoints();
		int numPoints = sgPoints.size();
        this.color = color;
        this.maxX = maxX;
        this.maxY = maxY;

		if (numPoints > 0) {
            // TODO: Set a zoom level that is appropriate for the route length
			setZoomLevel(route,mv);
		    // Don't center when there are 3 routes.
		    // centerToRouteMidPoint(mv);
		}
	}

	// Note: This function is called continuously.  More discussed here:
	// http://stackoverflow.com/questions/2792263/draw-is-being-constantly-called-in-my-android-map-overlay
    // and
	// http://code.google.com/p/android/issues/detail?id=232
	// "Any animation which takes place over the map can cause draw() to get repeatedly called. 
	// In my case, an indeterminate progress bar (e.g. spinning circle) was causing this.  
	// Simple fix was to set the visibility to GONE (rather than INVISIBLE) of the progress bar 
	// when I was finished with it."
	// 
	// From my own testing, i found that the draw is called constantly, unless if i remove the
	// MyLocationOverlay.
	@Override
	public void draw(Canvas canvas, MapView mv, boolean shadow) {
		super.draw(canvas, mv, shadow);
		drawPath(mv, canvas);
	}

	private void drawPath(MapView mv, Canvas canvas) {
		int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(4);
		for (int i = 0; i < sgPoints.size(); i++) {
			Point point = new Point();
			mv.getProjection().toPixels(sgPoints.get(i).getGeoPoint(), point);
			x2 = point.x;
			y2 = point.y;
			
			// 
			if (i > 0) {
			    // Skip points that shouldn't show up on the screen
			    if (outOfScreen(x1, y1, x2, y2)) {
//			        Logger.debug(TAG, String.format("Skipping line from %d,%d to %d,%d", 
//			                x1, y1, x2, y2));
			    } else {
			        canvas.drawLine(x1, y1, x2, y2, paint);
			    }
			}
			x1 = x2;
			y1 = y2;
		}
	}
	
	private boolean outOfScreen(int x1, int y1, int x2, int y2) {
	    // to the left of the screen?
	    if ((x1 < 0) && (x2 < 0)) {
	        return true;
	    }
	    // to the right of the screen?
	    if ((x1 > maxX) && (x2 > maxX)) {
	        return true;
	    }
	    
        // to the top of the screen?
        if ((y1 < 0) && (y2 < 0)) {
            return true;
        }
        // to the bottom of the screen?
        if ((y1 > maxY) && (y2 > maxY)) {
            return true;
        }
	    
	    return false;
	}
	
	
	private void centerToRouteMidPoint(MapView mv) {
        int numPoints = sgPoints.size();

        int moveToLat = sgPoints.get(0).getGeoPoint().getLatitudeE6() + 
                ( (sgPoints.get(numPoints-1).getGeoPoint().getLatitudeE6() -
                        sgPoints.get(0).getGeoPoint().getLatitudeE6()) / 2);
        
        int moveToLong = sgPoints.get(0).getGeoPoint().getLongitudeE6() + 
                ( (sgPoints.get(numPoints-1).getGeoPoint().getLongitudeE6() -
                        sgPoints.get(0).getGeoPoint().getLongitudeE6()) / 2);

        GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

        MapController mapController = mv.getController();
        mapController.animateTo(moveTo);
	}
	
	/**
	 * @param route
	 * @param mv
	 * The algorithm for setting the zoom level according to distance:
	 * Find the midpoint of the distance.
	 * Make this the center of the Map.
	 * Restriction - Can't use any of the mapController methods involving GeoPoints as we are using an alternative SimpleGeoPoint
	 * Then set the zoom level according to the distance by trial and error.
	 */
	private void setZoomLevel(Route route, MapView mv){
		int totalDistance = (int)route.drivingDistanceMeters;
		int sourceLat = route.source.getGeoPoint().getLatitudeE6();
		int sourceLong = route.source.getGeoPoint().getLongitudeE6();
	
		int destLat = route.destination.getGeoPoint().getLatitudeE6();
		int destLong = route.destination.getGeoPoint().getLongitudeE6();
		
		mv.getController().zoomToSpan(Math.abs(sourceLat-destLat), Math.abs(sourceLong-destLong));
		mv.getController().animateTo(new GeoPoint((sourceLat+destLat)/2, (sourceLong+destLong)/2));
		
	}
}
