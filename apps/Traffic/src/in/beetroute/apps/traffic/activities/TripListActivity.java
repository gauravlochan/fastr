package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.db.TripDbHelper;
import in.beetroute.apps.traffic.db.TripDbHelper.TripTable;
import in.beetroute.apps.traffic.trip.Trip;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

// TODO: Arrows like http://mfarhan133.wordpress.com/2010/10/14/list-view-tutorial-for-android/
public class TripListActivity extends Activity {

    protected ListView _listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routelist); 
        
        _listView = (ListView) findViewById(R.id.list);
        
        TripDbHelper tripDbHelper = new TripDbHelper(this, null);
        
        Trip lastTrip = Trip.getLastTrip(this);
        Trip nextTrip = Trip.getNextTrip(this, lastTrip);
        if (nextTrip != null) {
            tripDbHelper.insertTrip(nextTrip);
        }
        
        // From http://www.vogella.de/articles/AndroidListView/article.html#cursor
        // TODO Join this with the LocationUpdates
        Cursor mCursor = tripDbHelper.getAll();
        startManagingCursor(mCursor);
        
        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        ListAdapter adapter = new SimpleCursorAdapter(this, // Context.
                android.R.layout.two_line_list_item, // Specify the row template
                                                        // to use (here, two
                                                        // columns bound to the
                                                        // two retrieved cursor
                                                        // rows).
                mCursor, // Pass in the cursor to bind to.
                // Array of cursor columns to bind to.
                new String[] { 
                    TripTable.COLUMN_NAME_START_NAME,
                    TripTable.COLUMN_NAME_END_NAME },
                // Parallel array of which template objects to bind to those
                // columns.
                new int[] { android.R.id.text1, android.R.id.text2 });

        _listView.setAdapter(adapter);
        
        _listView.setOnItemClickListener( new SelectTripClickHandler() );
    }

    /**
     * Validates the input address/place.  If something is wrong, asks the user to fix it
     * otherwise moves on and does something with the input
     */
    public class SelectTripClickHandler implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            
            Intent intent = new Intent(TripListActivity.this, PlotTripActivity.class);
            intent.putExtra(AppGlobal.TRIP_KEY, position);
            startActivity(intent);
        }
    }
             
}
