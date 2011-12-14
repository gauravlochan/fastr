package in.fastr.apps.traffic.activities;

import in.fastr.apps.traffic.CongestionPoint;
import in.fastr.apps.traffic.Route;
import in.fastr.apps.traffic.btis.BtisCongestionService;
import in.fastr.apps.traffic.services.CongestionService;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

// Task so as not to block the UI thread
public class GetCongestionTask extends AsyncTask<Route, Void, List<CongestionPoint>> {
	private Context context;
	public GetCongestionTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected List<CongestionPoint> doInBackground(Route... params) {
		// All these are not really used by Btis service but will be used when calling our server
//		Route route = params[0];
//    	Gson gson = new Gson();
//    	String jsonOutput = gson.toJson(route.getPoints());
    	
    	CongestionService congestionSvc = new BtisCongestionService();
    	List<CongestionPoint> points = congestionSvc.getCongestionPoints();
    	
    	return points;
	}
	
	@Override
	protected void onPostExecute(List<CongestionPoint> result) {
		
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	
}
