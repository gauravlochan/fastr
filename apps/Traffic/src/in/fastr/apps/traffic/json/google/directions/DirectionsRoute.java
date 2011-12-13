package in.fastr.apps.traffic.json.google.directions;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DirectionsRoute {
    public Bounds bounds;

    public String copyrights;
    
    public List<Leg> legs;

    @SerializedName("overview_polyline")
    public Polyline overviewPolyline;

    public String summary;
    
    // TODO: Need to parse this json object to understand what it is.  temporarily make it string
    public List<String> warnings;

    // TODO: Need to parse this json object to understand what it is.  temporarily make it string
    @SerializedName("waypoint_order")
    public List<String> waypointOrder;
}
