package in.fastr.apps.traffic;

import greendroid.app.GDActivity;
import android.os.Bundle;
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
        			Toast.makeText(EnterAddressActivity.this, nameOfPlace, Toast.LENGTH_SHORT).show();
    			}
    		} else {
    			if (nameOfPlace.length() == 0) {
    			    Toast.makeText(EnterAddressActivity.this, destinationAddress, Toast.LENGTH_SHORT).show();
    			} else {
    			    Toast.makeText(EnterAddressActivity.this, R.string.bothspecifiederror, Toast.LENGTH_SHORT).show();
    			}
    		}
    	}
    }

	
	// User can type something and select 'done'
	
	// If not entered anything, error toast
	
}
