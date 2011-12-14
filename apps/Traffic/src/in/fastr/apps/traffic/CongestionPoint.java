package in.fastr.apps.traffic;

/**
 * A congestion point reported by some service.
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
    
}

