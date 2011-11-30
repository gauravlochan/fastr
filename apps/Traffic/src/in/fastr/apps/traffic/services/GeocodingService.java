package in.fastr.apps.traffic.services;

import com.google.android.maps.GeoPoint;

public interface GeocodingService {
	GeoPoint resolveAddress(String address);

}
