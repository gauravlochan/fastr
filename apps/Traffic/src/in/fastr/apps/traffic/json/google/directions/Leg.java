package in.fastr.apps.traffic.json.google.directions;

import in.fastr.apps.traffic.json.google.GoogleLatlong;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Leg {
	TextValueObject distance;
	TextValueObject duration;

    @SerializedName("end_address")
    public String endAddress;

    @SerializedName("end_location")
	GoogleLatlong endLocation;
	
    @SerializedName("start_address")
    public String startAddress;

    @SerializedName("start_location")
	GoogleLatlong startLocation;
	
	public List<Step> steps;
	
    @SerializedName("via_waypoint")
	public List<String> viaWaypoint;

}
