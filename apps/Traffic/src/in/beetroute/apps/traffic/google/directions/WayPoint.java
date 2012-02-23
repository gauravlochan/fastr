package in.beetroute.apps.traffic.google.directions;

import in.beetroute.apps.traffic.google.GoogleLatlong;

import com.google.gson.annotations.SerializedName;

public class WayPoint {
    public GoogleLatlong location;

    @SerializedName("step_index")
    public Integer stepIndex;
    
    @SerializedName("step_interpolation")
    public Double stepInterpolation;
}
