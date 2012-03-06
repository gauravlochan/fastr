package in.beetroute.apps.findme;

import in.beetroute.apps.traffic.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PlotRouteMap extends Activity {
	private WebView webView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.plotroutemap);
			Bundle extras = getIntent().getExtras();
			String toAddress = extras.getString("latlon");
			String fromAddress = getFromAddress(getApplicationContext());
			setupWebView(fromAddress, toAddress);
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	        webView.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	
	public void setupWebView(String fromAddress, String toAddress) {
		webView = (WebView)findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		String url = "file:///android_asset/mapView.html" + "?from=" + fromAddress + "&to=" + toAddress;
		//String url = "file:///android_asset/mapView.html";
		System.out.println("URL:::" + url);
		webView.loadUrl(url);
		webView.setWebViewClient(new client());
		
	}
	
	public String getFromAddress(Context context) {
		String latlon = "";
		try {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = locationManager.getAllProviders();
			// Add code to check if GPS is on and if it's not, provide a popup with the message 
			// "Can we turn on and turn off the GPS just to get your location"

			if(!providers.isEmpty()) {
				if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) ){
					Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					latlon = new String(String.valueOf(latitude) + ":" + String.valueOf(longitude));
					return latlon;
				} else {
					return "";				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return latlon;
	}
	
	private class client extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
	
}


