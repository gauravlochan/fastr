package com.swblr;

import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {
	public String routeData = new String();
	
	public void onLocationChanged(Location location) {
		// Called when a new location is found by the network location
		// provider.
		makeUseOfNewLocation(location);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}
	
	private void makeUseOfNewLocation(Location location) {
		double lat=location.getLatitude();
		double longitude=location.getLongitude();
		long timestamp=location.getTime();
		String stimestamp =Long.toString(timestamp);
		Date expiry = new Date(Long.parseLong(stimestamp));
		float currentSpeed=location.getSpeed();
		routeData+= String.format("%f,%f,%d,%f\n", lat, longitude, timestamp, currentSpeed);
		String details="Latitidue "+Double.toString(lat)+"Longitude "+Double.toString(longitude)+"Time "+expiry.toString()+"Speed"+String.format("%.2f",currentSpeed);
		Log.d("FASTTRIP", details);
	}
}
