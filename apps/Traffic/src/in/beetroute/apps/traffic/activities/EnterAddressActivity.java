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
    	boolean isDestination;

    	public DestLookupParams(String destination, DestinationType destType, boolean isDest) {
    		this.destination = destination;
    		this.destType = destType;
    		this.isDestination = isDest;
    	}
    }
    private MapPoint sourcePoint;
	private EditText _sourceAddress;
	private EditText _nameOfPlace;
	private Button _button;
	private Button changeSourceButton;
	
	private Dialog _manyDestinationsDialog; 
	private MapPointList _pointList;

	public class MapPointList{
		public List<MapPoint> points;
		public boolean isDestination;
		
		public MapPointList() {
			points = null;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.enteraddress);
        
        _button = ( Button ) findViewById( R.id.getDirectionsButton );
        _button.setOnClickListener( new GetDirectionsClickHandler() );

        changeSourceButton = ( Button ) findViewById(R.id.changeSourceButton);
        changeSourceButton.setOnClickListener(new ChangeSourceClickHandler());
        
        _sourceAddress = (EditText) findViewById( R.id.destAddressEditText );
        _nameOfPlace = (EditText) findViewById( R.id.placeEditText );

	}

	/**
	 * Validates the input address/place.  If something is wrong, asks the user to fix it
	 * otherwise moves on and does something with the input
	 */
    public class GetDirectionsClickHandler implements View.OnClickListener 
    {
    	public void onClick( View view ) {
    		
    		String nameOfPlace = _nameOfPlace.getText().toString();
    		if(nameOfPlace.length() == 0){
    			Toast.makeText(EnterAddressActivity.this, R.string.emptyaddresserror, Toast.LENGTH_SHORT).show();
    		} else {
    			DestLookupParams destParams = new DestLookupParams(nameOfPlace, DestinationType.PLACE, true);
				new PointLookupTask().execute(destParams);
    		}
    	}
    }
    
    public class ChangeSourceClickHandler implements View.OnClickListener
    {
    	public void onClick(View view){
    		String sourceAddress = _sourceAddress.getText().toString();
    		if(sourceAddress.length() != 0){
    			DestLookupParams destParams = new DestLookupParams(sourceAddress, DestinationType.PLACE, false);
				new PointLookupTask().execute(destParams);
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
	public class PointLookupTask extends AsyncTask<DestLookupParams, Void, MapPointList> {
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
		protected MapPointList doInBackground(DestLookupParams... params) {
	        Logger.debug(TAG, "Calling poiService");
	        MapPointList pointList = new MapPointList();	

	        switch (params[0].destType) {
	        case PLACE:
		        PointOfInterestService poiService = new OnzePointOfInterestService();
		    	pointList.points = poiService.getPoints(params[0].destination);
		    	break;

	        case ADDRESS:
	        	GeocodingService geoService = new AndroidGeocodingService(EnterAddressActivity.this);
	        	pointList.points = geoService.resolveAddress(params[0].destination);
	            break;      	
	        	
	        }
	        Logger.debug(TAG, "Called poiService");
	        if(params[0].isDestination){
	        	pointList.isDestination = true;
	        }else{
	        	pointList.isDestination = false;
	        }
			return pointList;
		}

		@Override
		protected void onPostExecute(MapPointList pointList) {
			this.progressDialog.cancel();
			
			// If nothing was found, stay on the activity and let user try again
			if (pointList.points.size() == 0) {
   			    Toast.makeText(EnterAddressActivity.this, "No results found, try again", Toast.LENGTH_LONG).show();
   			    return;
			}
			
			// If one point was found
			if (pointList.points.size() == 1) {
	            Intent data = new Intent();
	            if(pointList.isDestination){
	            	data.putExtra(AppGlobal.destPoint, pointList.points.get(0));
	            	if(sourcePoint!=null){
	            		data.putExtra(AppGlobal.sourcePoint, sourcePoint);
	            	}
		            setResult(RESULT_OK, data);
		            finish();
	            }else{
	            	sourcePoint = pointList.points.get(0);
	            }

	            return;
			}

			// Multiple points were found.  Let the activity handle that
			Message msg = new Message();
			msg.obj = pointList;
            EnterAddressActivity.this.manyResultsHandler.dispatchMessage(msg);
		}
	}
	
	private void finishWithSelectedPoint(int item, MapPointList pointList) {
        Intent data = new Intent();
        if(pointList.isDestination){
	        data.putExtra(AppGlobal.destPoint, pointList.points.get(item));
	        if(sourcePoint!=null){
	        	data.putExtra(AppGlobal.sourcePoint, sourcePoint);
	        }
	        setResult(RESULT_OK, data);
	        finish();
        }else{
        	sourcePoint = pointList.points.get(item);
        }

        return;
	}
	
    /**
     * Handler for when the destination lookup yields many results
     */
    Handler manyResultsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MapPointList pointList = (MapPointList) msg.obj;
            if(pointList.isDestination){
            	EnterAddressActivity.this.displayDialog(pointList, "Pick Destination:");
            }else{
            	EnterAddressActivity.this.displayDialog(pointList, "Pick Source:");
            }
        }
    };
    
    // http://stackoverflow.com/questions/2874191/is-it-possible-to-create-listview-inside-dialog
    // http://www.vogella.de/articles/AndroidListView/article.html
    // http://mylifewithandroid.blogspot.com/2008/03/my-first-meeting-with-simpleadapter.html
    private void displayDialog(MapPointList pointList, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(msg);

        SimpleAdapter resultsAdapter = new SimpleAdapter(this,
                getSimpleAdapterList(pointList.points),
                android.R.layout.two_line_list_item, 
                new String[] { POINT_NAME, POINT_DESC }, 
                new int[] { android.R.id.text1, android.R.id.text2 }
        );

        ListView modeList = new ListView(this);
        modeList.setAdapter(resultsAdapter);

        builder.setView(modeList);
        
        // ugly to make this a class member, but i couldn't think of any other way 
        // to get access to this data inside of OnItemClickListener
        _pointList = pointList;
        
        // ugly to make this a class member, but i couldn't think of any other way 
        // to get access to this data inside of OnItemClickListener
        _manyDestinationsDialog = builder.create();
        _manyDestinationsDialog.show();

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                
                finishWithSelectedPoint(arg2, _pointList);
                _manyDestinationsDialog.dismiss();
            }
        });
    }

    
 }
