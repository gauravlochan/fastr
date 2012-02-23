package in.beetroute.apps.traffic.btis;

import com.google.gson.annotations.SerializedName;

// {"label":"White Field RW Station","lat":"12.99602778","lon":"77.76161111","status":"Slow","smscode":"WFRL"}]
class BtisLocation {
	public String label;

	@SerializedName("lat")
	public Double latitude;

	@SerializedName("lon")
	public Double longitude;
	
	public String status;
	
	public String smscode;
}
