package in.beetroute.apps.traffic.activities;

import in.beetroute.apps.traffic.R;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RouteListActivity extends Activity {

    protected ListView _listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routelist); 
        
        _listView = (ListView) findViewById(R.id.list);
        
        // From http://www.vogella.de/articles/AndroidListView/article.html#cursor
        Cursor mCursor = getContacts();
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
                new String[] { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME },
                // Parallel array of which template objects to bind to those
                // columns.
                new int[] { android.R.id.text1, android.R.id.text2 });

        _listView.setAdapter(adapter);
        
    }
    
    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }
         
}
