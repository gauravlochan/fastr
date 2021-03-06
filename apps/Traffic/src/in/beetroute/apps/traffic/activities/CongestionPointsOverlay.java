package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.TrafficStatus;
import in.beetroute.apps.traffic.backend.CongestionPoint;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CongestionPointsOverlay extends ItemizedOverlay<OverlayItem> {
    private static final String TAG = Global.COMPANY;

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;

    public CongestionPointsOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
    }

    public void addCongestionPoints(List<CongestionPoint> points) {
        // Create OverlayItems for each point
        for (int i = 0; i < points.size(); i++) {
            CongestionPoint point = points.get(i);
            // TODO: This reported by string is hardcoded to BTIS, it should pick it up from
            // somewhere
            String snippet = "Reported by Bangalore Traffic Information System at " + point.reportedAt;
            OverlayItem overlayItem = new OverlayItem(
                    point.location.getGeoPoint(), 
                    point.name, 
                    snippet);

            // Set marker color depending on the traffic status
            Drawable drawable = getColoredIcon(point.status);
            boundCenterBottom(drawable);
            overlayItem.setMarker(drawable);

            mOverlays.add(overlayItem);
        }
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        OverlayItem item = mOverlays.get(index);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.show();
        return true;
    }
    
    private Drawable getColoredIcon(TrafficStatus status) {
        if (status == TrafficStatus.RED) {
            return mContext.getResources().getDrawable(R.drawable.marker_red);
        }
        if (status == TrafficStatus.YELLOW) {
            return mContext.getResources().getDrawable(R.drawable.marker_orange);
        }
        if (status == TrafficStatus.GREEN) {
            return mContext.getResources().getDrawable(R.drawable.marker_green);
        }
        
        return mContext.getResources().getDrawable(R.drawable.gd_map_pin_dot);
    }
    
}
