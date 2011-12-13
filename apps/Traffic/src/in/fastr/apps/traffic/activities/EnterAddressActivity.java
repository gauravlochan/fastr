package in.fastr.apps.traffic.activities;

import greendroid.app.GDActivity;
import in.fastr.apps.traffic.AppGlobal;
import in.fastr.apps.traffic.MapPoint;
import in.fastr.apps.traffic.R;
import in.fastr.apps.traffic.google.geocoding.AndroidGeocodingService;
import in.fastr.apps.traffic.onze.OnzePointOfInterestService;
import in.fastr.apps.traffic.services.GeocodingService;
import in.fastr.apps.traffic.services.PointOfInterestService;
import in.fastr.library.Global;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The screen where user enters address for route
 * - User can type in an address and click
 * - If user doesnt type in address, pop up warning and keep user here
 * - User can press back.  If they come back here, they will start afresh
 *   (TODO: store state)
 * - TODO: Autocomplete addresses
 * 
 * @author gauravlochan
 */
public class EnterAddressActivity extends GDActivity {
   	enum DestinationType { PLACE, ADDRESS };
    class DestLookupParams {
    	String destination;
    	DestinationType destType;

    	public DestLookupParams(String destination, DestinationType destType) {
    		this.destination = destination;
    		this.destType = destType;
    	}
    }

	
	private EditText _destinationAddress;
	private EditText _nameOfPlace;
	private Button _button;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.enteraddress);
        
        _button = ( Button ) findViewById( R.id.getDirectionsButton );
        _button.setOnClickListener( new GetDirectionsClickHandler() );

        _destinationAddress = (EditText) findViewById( R.id.destAddressEditText );
        _nameOfPlace = (EditText) findViewById( R.id.placeEditText );

	}

    public class GetDirectionsClickHandler implements View.OnClickListener 
    {
    	public void onClick( View view ) {
    		String destinationAddress = _destinationAddress.getText().toString();
    		String nameOfPlace = _nameOfPlace.getText().toString();
    		
    		if (destinationAddress.length() == 0) {
    			if (nameOfPlace.length() == 0) {
    			    Toast.makeText(EnterAddressActivity.this, R.string.emptyaddresserror, Toast.LENGTH_SHORT).show();
    			} else {
    				DestLookupParams destParams = new DestLookupParams(nameOfPlace, DestinationType.PLACE);
    				new PointLookupTask().execute(destParams);
    			}
    		} else {
    			if (nameOfPlace.length() == 0) {
    				DestLookupParams destParams = new DestLookupParams(destinationAddress, DestinationType.ADDRESS);
    				new PointLookupTask().execute(destParams);
    			} else {
    			    Toast.makeText(EnterAddressActivity.this, R.string.bothspecifiederror, Toast.LENGTH_SHORT).show();
    			}
    		}
    	}
    }
    

	public class PointLookupTask extends AsyncTask<DestLookupParams, Void, List<MapPoint>> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			this.progressDialog = ProgressDialog.show(
					EnterAddressActivity.this,
					"Please wait...", // title
					"Calculating the best route", // message
					true // indeterminate
					);
		}

		@Override
		protected List<MapPoint> doInBackground(DestLookupParams... params) {
	        Log.d(Global.Company, "Calling poiService");
	        List<MapPoint> points = null;

	        switch (params[0].destType) {
	        case PLACE:
		        PointOfInterestService poiService = new OnzePointOfInterestService();
		    	points = poiService.getPoints(params[0].destination);
		    	break;

	        case ADDRESS:
	        	GeocodingService geoService = new AndroidGeocodingService(EnterAddressActivity.this);
	          	points = geoService.resolveAddress(params[0].destination);
	            break;      	
	        	
	        }
	        Log.d(Global.Company, "Called poiService");
			return points;
		}

		@Override
		protected void onPostExecute(List<MapPoint> points) {
			this.progressDialog.cancel();
			
			// If nothing was found, stay on the activity and let user try again
			if (points.size() == 0) {
   			    Toast.makeText(EnterAddressActivity.this, "No results found, try again", Toast.LENGTH_LONG).show();
   			    return;
			}

			// TODO: Populate all points of interest and ask user to pick
			if (points.size() > 1) {
   			    Toast.makeText(EnterAddressActivity.this, "Multiple results found, picking the first", Toast.LENGTH_SHORT).show();
   			}
	
			Intent data = new Intent();
			data.putExtra(AppGlobal.destPoint, points.get(0));
			setResult(RESULT_OK, data);
			finish();
		}
	}
	
 }
