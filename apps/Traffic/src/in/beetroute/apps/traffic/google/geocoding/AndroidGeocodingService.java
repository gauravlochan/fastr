package in.beetroute.apps.traffic.google.geocoding;

import in.beetroute.apps.commonlib.SimpleGeoPoint;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.services.GeocodingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class AndroidGeocodingService implements GeocodingService {
	private Context context;
	
	public AndroidGeocodingService(Context context) {
		this.context = context;
	}

	@Override
	public List<MapPoint> resolveAddress(String addressText) {
		Geocoder gc = new Geocoder(context);
        ArrayList<MapPoint> list = new ArrayList<MapPoint>();
		
        List<Address> foundAdresses = null;
		try {
			foundAdresses = gc.getFromLocationName(addressText, 5);
		} catch (IOException e) {
		    return list;
		}

		for (Address address : foundAdresses) {
			MapPoint point = new MapPoint(address.getFeatureName(), 
					addressText, address.getLatitude(),
					address.getLongitude());
			list.add(point);
		}
		
		return list;
	}
	
	public List<Address> resolveLocation(SimpleGeoPoint sgPoint, int maxResults) {
	    Geocoder gc = new Geocoder(context);
	    try {
            return gc.getFromLocation(sgPoint.getLatitude(), sgPoint.getLongitude(), maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        }

	    // on exception, return empty array
        return new ArrayList<Address>();

	}

}
