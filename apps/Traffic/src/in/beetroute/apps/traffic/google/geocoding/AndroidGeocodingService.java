package in.beetroute.apps.traffic.google.geocoding;

import in.beetroute.apps.commonlib.ServiceProviders;
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
		
        List<Address> foundAdresses = null;
		try {
			foundAdresses = gc.getFromLocationName(addressText, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<MapPoint> list = new ArrayList<MapPoint>();
		for (Address address : foundAdresses) {
			MapPoint point = new MapPoint(ServiceProviders.ANDROID, "",
					address.getFeatureName(), addressText);
			point.setLocation(address.getLatitude(), address.getLongitude());
			list.add(point);
		}
		
		return list;
	}

}
