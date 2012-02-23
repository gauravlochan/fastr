package in.beetroute.apps.traffic.services;

import in.beetroute.apps.traffic.MapPoint;

import java.util.List;

public interface GeocodingService {
	List<MapPoint> resolveAddress(String address);
}
