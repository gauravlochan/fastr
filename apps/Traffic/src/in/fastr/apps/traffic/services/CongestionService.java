package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.server.CongestionPoint;

import java.util.List;

public interface CongestionService {
	// TODO: What arguments should be sent in eventually? Route? Single point?
	public List<CongestionPoint> getCongestionPoints();
	

}
