package in.beetroute.apps.traffic.activities;

import greendroid.app.GDActivity;
import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.google.geocoding.AndroidGeocodingService;
import in.beetroute.apps.traffic.onze.OnzePointOfInterestService;
import in.beetroute.apps.traffic.services.GeocodingService;
import in.beetroute.apps.traffic.services.PointOfInterestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * The screen where user enters address for route
 * - User can type in an address and click
 * - If user doesnt type in address, pop up warning and keep user here
 * - User can press back.  If they come back here, they will start afresh
 * - TODO: Autocomplete addresses
 * 
 * @author gauravlochan
 */
public class EnterAddressActivity extends GDActivity {
    private static final String TAG = Global.COMPANY;

    final static String POINT_NAME = "name";
    final static String POINT_DESC = "description";
    
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
	
	private Dialog _manyDestinationsDialog; 
	private List<MapPoint> _points;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.enteraddress);
        
        _button = ( Button ) findViewById( R.id.getDirectionsButton );
        _button.setOnClickListener( new GetDirectionsClickHandler() );

        _destinationAddress = (EditText) findViewById( R.id.destAddressEditText );
        _nameOfPlace = (EditText) findViewById( R.id.placeEditText );

	}

	/**
	 * Validates the input address/place.  If something is wrong, asks the user to fix it
	 * otherwise moves on and does something with the input
	 */
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
    
    /**
     * Convert the point data into the structure that the SimpleAdapter understands
     * @param points
     * @return
     */
    private ArrayList<HashMap<String,String>> getSimpleAdapterList(List<MapPoint> points) {
        int numPoints = points.size();
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>(numPoints);
        
        for (int i=0; i < numPoints; i++) {
            MapPoint point = points.get(i);
            HashMap<String,String> pointMap = new HashMap<String,String>();
            pointMap.put(POINT_NAME, point.getName());
            pointMap.put(POINT_DESC, point.getDescription());
            list.add(pointMap);
        }
        return list;
    }
    

    /**
     * Task takes in the destination (whether an address or a place) and calls the appropriate
     * service to resolve it, in the meantime displaying and updating the progress dialog.
     * 
     * Once the result comes in, handles it
     */
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
	        Logger.debug(TAG, "Calling poiService");
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
	        Logger.debug(TAG, "Called poiService");
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
			
			// If one point was found
			if (points.size() == 1) {
	            Intent data = new Intent();
	            data.putExtra(AppGlobal.destPoint, points.get(0));
	            setResult(RESULT_OK, data);
	            finish();
	            return;
			}

			// Multiple points were found.  Let the activity handle that
			Message msg = new Message();
			msg.obj = points;
            EnterAddressActivity.this.manyResultsHandler.dispatchMessage(msg);
		}
	}
	
	private void finishWithSelectedPoint(int item, List<MapPoint> points) {
        Intent data = new Intent();
        data.putExtra(AppGlobal.destPoint, points.get(item));
        setResult(RESULT_OK, data);
        finish();
        return;
	}
	
    /**
     * Handler for when the destination lookup yields many results
     */
    Handler manyResultsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            List<MapPoint> points = (List<MapPoint>) msg.obj;
            EnterAddressActivity.this.displayDialog(points);
        }
    };
    
    // http://stackoverflow.com/questions/2874191/is-it-possible-to-create-listview-inside-dialog
    // http://www.vogella.de/articles/AndroidListView/article.html
    // http://mylifewithandroid.blogspot.com/2008/03/my-first-meeting-with-simpleadapter.html
    private void displayDialog(List<MapPoint> points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multiple results, pick the best one:");

        SimpleAdapter resultsAdapter = new SimpleAdapter(this,
                getSimpleAdapterList(points),
                android.R.layout.two_line_list_item, 
                new String[] { POINT_NAME, POINT_DESC }, 
                new int[] { android.R.id.text1, android.R.id.text2 }
        );

        ListView modeList = new ListView(this);
        modeList.setAdapter(resultsAdapter);

        builder.setView(modeList);
        
        // ugly to make this a class member, but i couldn't think of any other way 
        // to get access to this data inside of OnItemClickListener
        _points = points;
        
        // ugly to make this a class member, but i couldn't think of any other way 
        // to get access to this data inside of OnItemClickListener
        _manyDestinationsDialog = builder.create();
        _manyDestinationsDialog.show();

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                
                finishWithSelectedPoint(arg2, _points);
                _manyDestinationsDialog.dismiss();
            }
        });
    }

    
 }
