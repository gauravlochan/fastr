package in.beetroute.apps.traffic.services;

import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;

import java.util.List;

import android.location.Address;

public interface GeocodingService {
	List<MapPoint> resolveAddress(String address);
    List<Address> resolveLocation(SimpleGeoPoint sgPoint, int maxResults);
	
}
