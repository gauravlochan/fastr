package in.fastr.apps.traffic.activities;

import in.fastr.apps.traffic.CongestionPoint;
import in.fastr.apps.traffic.R;
import in.fastr.apps.traffic.TrafficStatus;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CongestionPointsOverlay extends ItemizedOverlay<OverlayItem> {
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
            String snippet = "Reported by " + point.reportedBy + " at " + point.reportedAt;
            OverlayItem overlayItem = new OverlayItem(
                    point.location.getGeoPoint(), 
                    point.name, 
                    snippet);

            // Set marker color depending on the traffic status
            // TODO: Is there a more optimal way to create all the drawables once
            Drawable drawable = getColoredIcon(point.status);
            boundCenterBottom(drawable);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
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
            return mContext.getResources().getDrawable(R.drawable.red);
        }
        if (status == TrafficStatus.YELLOW) {
            return mContext.getResources().getDrawable(R.drawable.yellow);
        }
        if (status == TrafficStatus.GREEN) {
            return mContext.getResources().getDrawable(R.drawable.green);
        }
        
        return mContext.getResources().getDrawable(R.drawable.gd_map_pin_dot);
    }
    
}
