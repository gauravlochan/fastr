package in.beetroute.apps.traffic.services;

import in.beetroute.apps.traffic.backend.CongestionPoint;

import java.util.List;

public interface CongestionService {
	// TODO: What arguments should be sent in eventually? Route? Single point?
	public List<CongestionPoint> getCongestionPoints();
	

}
