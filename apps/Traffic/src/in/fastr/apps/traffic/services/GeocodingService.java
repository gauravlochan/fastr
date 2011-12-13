package in.fastr.apps.traffic.services;

import java.util.List;

public interface GeocodingService {
	List<MapPoint> resolveAddress(String address);
}
