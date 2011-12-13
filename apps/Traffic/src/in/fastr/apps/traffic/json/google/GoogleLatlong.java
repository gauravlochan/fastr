package in.fastr.apps.traffic.json.google;

import com.google.gson.annotations.SerializedName;

public class GoogleLatlong {
    @SerializedName("lat")
    public Double latitude;

    @SerializedName("lng")
    public Double longitude;
}
