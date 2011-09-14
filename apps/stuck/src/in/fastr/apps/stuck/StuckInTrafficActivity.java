package in.fastr.apps.stuck;

import in.fastr.apps.common.UploadRecords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class StuckInTrafficActivity extends Activity {
    private DbWrapper dbWrapper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbWrapper = new DbWrapper(this);
        dbWrapper.createDatabase();
    }

    /**
     * OnCLick Listener for the StuckInTraffic Button
     */
    public void onStuckButtonClicked(View view) {
        // Kick off the task to get the current location
        // TODO Can this retriever object be accidentally garbage collected???
        LocationRetriever retriever = new LocationRetriever(dbWrapper, 
                (LocationManager)getSystemService(Context.LOCATION_SERVICE));
        
        retriever.startLocationQuery(this);
        
        // Open the new tabs for the users amusement
        Intent intent = new Intent().setClass(this, TabPage.class);
        startActivity(intent);
    }
    
    /**
     * OnCLick Listener for the Upload Button
     * Invoked from main.xml
     */
    public void onUploadButtonClicked(View view) {
        UploadRecords.upload(dbWrapper);
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(App.Name, "KILL");
    }
   
}
