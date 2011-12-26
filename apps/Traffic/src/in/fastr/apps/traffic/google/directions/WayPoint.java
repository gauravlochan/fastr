package in.fastr.apps.traffic.google.directions;

import in.fastr.apps.traffic.google.GoogleLatlong;

import com.google.gson.annotations.SerializedName;

public class WayPoint {
    public GoogleLatlong location;

    @SerializedName("step_index")
    public Integer stepIndex;
    
    @SerializedName("step_interpolation")
    public Double stepInterpolation;
}
