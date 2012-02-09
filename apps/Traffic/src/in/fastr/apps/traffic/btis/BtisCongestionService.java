package in.fastr.apps.traffic.btis;

import in.fastr.apps.traffic.TrafficStatus;
import in.fastr.apps.traffic.backend.CongestionPoint;
import in.fastr.apps.traffic.services.CongestionService;
import in.fastr.library.Global;
import in.fastr.library.RESTHelper;
import in.fastr.library.ServiceProviders;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;

public class BtisCongestionService implements CongestionService {
	private static final String serviceUrl = "http://www.btis.in/trafficstatus_cache.txt";

	@Override
	public List<CongestionPoint> getCongestionPoints() {
		String jsonResult = RESTHelper.simpleGet(serviceUrl);
		
		Gson gson = new Gson();
		BtisResult result = gson.fromJson(jsonResult, BtisResult.class);
		
		if (result == null) {
		    Log.e(Global.Company, "Congestion data from BTIS is missing or the wrong json format");
		    return null;
		} else {
    		List<CongestionPoint> congestionPoints = new ArrayList<CongestionPoint>(result.locations.size());
    		for (BtisLocation location: result.locations) {
    			TrafficStatus status = getTrafficStatus(location.status);
    			CongestionPoint point = new CongestionPoint(ServiceProviders.BTIS, result.time, status);
    			point.identifier = location.smscode;
    			point.setLocation(location.latitude, location.longitude);
    			point.name = location.label;
    			congestionPoints.add(point);
    		}
    		return congestionPoints;
		}
		
	}

	private TrafficStatus getTrafficStatus(String status) {
		if (status.equalsIgnoreCase("Smooth")) return TrafficStatus.GREEN;
		if (status.equalsIgnoreCase("Slow")) return TrafficStatus.YELLOW;
		if (status.equalsIgnoreCase("Delay")) return TrafficStatus.RED;
		
		return TrafficStatus.UNSPECIFIED;
	}

}
