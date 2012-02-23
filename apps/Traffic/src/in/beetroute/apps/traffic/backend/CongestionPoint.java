package in.beetroute.apps.traffic.backend;

import in.beetroute.apps.commonlib.ServiceProviders;
import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.TrafficStatus;


/**
 * A congestion point reported by some service
 * 
 * @author gauravlochan
 */
public class CongestionPoint {
	public ServiceProviders reportedBy;
	public String identifier; // service specific identifier
	public String name;

    // TODO: Represent as long epochTime?
	public String reportedAt;
    public SimpleGeoPoint location;
    public TrafficStatus status;

    public CongestionPoint(ServiceProviders provider, String reportTime, TrafficStatus status) {
    	this.reportedBy = provider;
    	this.reportedAt = reportTime;
    	this.status = status;
    }

	public void setLocation(double latitude, double longitude) {
		this.location = new SimpleGeoPoint(latitude, longitude);
	}
    
    public double getLatitude() {
        return location.getLatitude();
    }
    
    public double getLongitude() {
        return location.getLongitude();
    }
    
    @Deprecated
    public long getEpochTime() {
        return 0;
    }

}

