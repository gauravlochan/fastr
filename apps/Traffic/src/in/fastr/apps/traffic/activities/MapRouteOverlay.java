package in.fastr.apps.traffic.activities;

import in.fastr.apps.traffic.SimpleGeoPoint;
import in.fastr.apps.traffic.services.Route;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

//From http://code.google.com/p/j2memaprouteprovider/ which i found from
//http://stackoverflow.com/questions/2023669/j2me-android-blackberry-driving-directions-route-between-two-locations
//
//Also check out http://stackoverflow.com/questions/1612533/android-drivingdirections-removed-since-api-1-0-how-to-do-it-in-1-5-1-6
public class MapRouteOverlay extends Overlay {
	List<SimpleGeoPoint> sgPoints;

	public MapRouteOverlay(Route route, MapView mv) {
		sgPoints = route.getPoints();
		int numPoints = sgPoints.size();

		if (numPoints > 0) {
			// Center to the midpoint of the route
			int moveToLat = sgPoints.get(0).getGeoPoint().getLatitudeE6() + 
					( (sgPoints.get(numPoints-1).getGeoPoint().getLatitudeE6() -
							sgPoints.get(0).getGeoPoint().getLatitudeE6()) / 2);
			
			int moveToLong = sgPoints.get(0).getGeoPoint().getLongitudeE6() + 
					( (sgPoints.get(numPoints-1).getGeoPoint().getLongitudeE6() -
							sgPoints.get(0).getGeoPoint().getLongitudeE6()) / 2);

			GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

			MapController mapController = mv.getController();
			mapController.animateTo(moveTo);
			// TODO: Set a zoom level that is appropriate for the route length
		}
	}

	@Override
	public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
		super.draw(canvas, mv, shadow);
		drawPath(mv, canvas);
		return true;
	}

	public void drawPath(MapView mv, Canvas canvas) {
		int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		for (int i = 0; i < sgPoints.size(); i++) {
			Point point = new Point();
			mv.getProjection().toPixels(sgPoints.get(i).getGeoPoint(), point);
			x2 = point.x;
			y2 = point.y;
			if (i > 0) {
				canvas.drawLine(x1, y1, x2, y2, paint);
			}
			x1 = x2;
			y1 = y2;
		}
	}
}
