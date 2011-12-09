package in.fastr.apps.traffic.activities;

import greendroid.app.GDActivity;
import in.fastr.apps.traffic.AppGlobal;
import in.fastr.apps.traffic.R;
import in.fastr.apps.traffic.services.GeocodingService;
import in.fastr.apps.traffic.services.GoogleGeocodingService;
import in.fastr.apps.traffic.services.LatlongPointOfInterestService;
import in.fastr.apps.traffic.services.PointOfInterest;
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
	
	private EditText _destinationAddress;
	private EditText _nameOfPlace;
	private Button _button;
	
	List<PointOfInterest> retrieved_points;
	
	
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
    				new PlaceLookupTask().execute(nameOfPlace);
    			}
    		} else {
    			if (nameOfPlace.length() == 0) {
    			    getAddress(destinationAddress);
    			} else {
    			    Toast.makeText(EnterAddressActivity.this, R.string.bothspecifiederror, Toast.LENGTH_SHORT).show();
    			}
    		}
    	}
    }
    

	public class PlaceLookupTask extends AsyncTask<String, Void, List<PointOfInterest>> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			this.progressDialog = ProgressDialog.show(
					EnterAddressActivity.this,
					"Please wait...locating place", // title
					"Requesting latitude and latitude for this place", // message
					true // indeterminate
					);
		}

		@Override
		protected List<PointOfInterest> doInBackground(String... params) {
	        Log.d(Global.Company, "Calling poiService");
	        PointOfInterestService poiService = new LatlongPointOfInterestService();
	    	List<PointOfInterest> points = poiService.getPoints(params[0]);

	        Log.d(Global.Company, "Called poiService");

			return points;
		}

		@Override
		protected void onPostExecute(List<PointOfInterest> points) {
			this.progressDialog.cancel();
			retrieved_points = points;
			
			Intent data = new Intent();
			
			// TODO: Populate all points of interest and ask user to pick
			data.putExtra(AppGlobal.destPointOfInterest, retrieved_points.get(0));
			setResult(RESULT_OK, data);
			finish();
		}
	}

    
    private void getAddress(String address) {
        Log.d(Global.Company, "Calling geoService");
        
    	GeocodingService geoService = new GoogleGeocodingService();
    	geoService.resolveAddress(address);
    	
        Log.d(Global.Company, "Called geoService");
        
        finish();
    }
	
 }
