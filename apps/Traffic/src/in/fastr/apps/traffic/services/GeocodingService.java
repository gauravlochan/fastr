package in.fastr.apps.traffic.services;

import in.fastr.apps.traffic.MapPoint;

import java.util.List;

public interface GeocodingService {
	List<MapPoint> resolveAddress(String address);
}
