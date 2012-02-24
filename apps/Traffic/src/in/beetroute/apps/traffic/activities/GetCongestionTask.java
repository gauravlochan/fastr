package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.Route;
import in.beetroute.apps.traffic.backend.CongestionPoint;
import in.beetroute.apps.traffic.btis.BtisCongestionService;
import in.beetroute.apps.traffic.services.CongestionService;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

// Task so as not to block the UI thread
public class GetCongestionTask extends AsyncTask<Route, Void, List<CongestionPoint>> {
    private static final String TAG = Global.COMPANY;

    private Context context;
    private MapView mapView;

    public GetCongestionTask(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    @Override
    protected List<CongestionPoint> doInBackground(Route... params) {
        // All these are not really used by Btis service but will be used when
        // calling our server
        // Route route = params[0];
        // Gson gson = new Gson();
        // String jsonOutput = gson.toJson(route.getPoints());

        List<CongestionPoint> points = null;
        try {
            CongestionService congestionSvc = new BtisCongestionService();
            points = congestionSvc.getCongestionPoints();
        } catch (Exception e) {
            Logger.error(TAG, "Error calling BTIS", e);
        }
        return points;
    }

    @Override
    protected void onPostExecute(List<CongestionPoint> result) {
        if (result != null) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.gd_map_pin_dot);
            CongestionPointsOverlay congestionOverlay = new CongestionPointsOverlay(
                    drawable, context);
    
            congestionOverlay.addCongestionPoints(result);
            List<Overlay> listOfOverlays = mapView.getOverlays();
            listOfOverlays.add(congestionOverlay);
            mapView.invalidate();
            
            Toast.makeText(context, "Marked congestion points", Toast.LENGTH_LONG).show();
        }
    }

}
