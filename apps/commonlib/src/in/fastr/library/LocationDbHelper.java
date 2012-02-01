package in.fastr.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * Helper class for using the location updates DB.
 * It extends SQLiteOpenHelper giving access to the DB object
 * 
 * @author gauravlochan
 */
public class LocationDbHelper extends SQLiteOpenHelper {
    private final String dbName;
    private enum UploadStatus {
        NEW,
        UPLOADING,
        UPLOADED
    };

    /**
     * A class that defines the table
     * 
     * @author gauravlochan
     */
    public static final class LocationUpdates implements BaseColumns {
        // This class cannot be instantiated
        private LocationUpdates() {}
        
        public static final String TABLE_NAME = "locationUpdates";

        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_TIMESTAMP = "Timestamp";
        public static final String COLUMN_NAME_SPEED = "Speed";
        public static final String COLUMN_NAME_UPLOAD_STATUS = "UploadStatus";
        
        public static String getSchema() {
            return _ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_NAME_LATITUDE + " Double, "
                    + COLUMN_NAME_LONGITUDE+ " Double, "
                    + COLUMN_NAME_TIMESTAMP+ " Double, "
                    + COLUMN_NAME_SPEED + " Double, "
                    + COLUMN_NAME_UPLOAD_STATUS + " Integer";
        }
        
        public static String getColumns() {
            return " (Latitude, Longitude, Timestamp, Speed, UploadStatus) ";
        }

    }

    public LocationDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbName = name;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Global.Company, "Attempting to create DB " + dbName);

        /* Create a Table in the Database. */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LocationUpdates.TABLE_NAME +
                "(" + LocationUpdates.getSchema() + ");");

        Log.d(Global.Company, "Successfully created DB");
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Global.Company, "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + LocationUpdates.TABLE_NAME);
        onCreate(db);
    }    
    
    
    /**
     * Insert a single reported location update
     */
    public void insertPoint(LocationUpdate point) {
        Log.d(Global.Company, "Attempting write point to DB " + dbName);
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("INSERT INTO " + LocationUpdates.TABLE_NAME + 
                    LocationUpdates.getColumns() + " VALUES ("
                    + point.getLatitude() + ", " 
                    + point.getLongitude() + ", " 
                    + point.getEpochTime() + ", " 
                    + point.getSpeed() + ", "
                    + UploadStatus.NEW + ");"
                    );
            
            Log.d(Global.Company, "Succesful write to DB");
        } finally {
            db.close();
        }

    }
    
    
    /**
     * Print out the database contents
     */
    public void logDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + LocationUpdates.TABLE_NAME, null);
    
            int Column1 = c.getColumnIndex(LocationUpdates.COLUMN_NAME_LATITUDE);
            int Column2 = c.getColumnIndex(LocationUpdates.COLUMN_NAME_LONGITUDE);
    
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    double lat = c.getDouble(Column1);
                    double lon = c.getDouble(Column2);
                    String coordinate = String.format("%f %f", lat, lon);
                    Log.d(Global.Company, coordinate);
                } while (c.moveToNext());
            }
        } finally {
            db.close();
        }
    }
    
    /**
     * Return count of location updates that have not been uploaded.
     * 
     * @return count of congestion points that have not been uploaded
     */
    public long countUnsyncedLocationUpdates() {
        SQLiteDatabase db = getReadableDatabase();
        
        try {
            SQLiteStatement stmt = db.compileStatement("SELECT COUNT(*) FROM "
                    + LocationUpdates.TABLE_NAME + " WHERE "
                    + LocationUpdates.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NEW
                    );
            long count = stmt.simpleQueryForLong();
            return count;
        } finally {
            db.close();
        }
    }
    
    
    /**
     * Loads a list of all unsynced location updates from the database.  
     *        
     * @return
     */
    public List<LocationUpdate> getUnsyncedLocationUpdates() {
        SQLiteDatabase db = getReadableDatabase();
        
        try {
            String query = "SELECT * FROM " + LocationUpdates.TABLE_NAME + " WHERE " 
                    + LocationUpdates.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NEW + ";";
            Cursor c = db.rawQuery(query, null);

            if (c != null) {
                int latIndex = c.getColumnIndex(LocationUpdates.COLUMN_NAME_LATITUDE);
                int longIndex = c.getColumnIndex(LocationUpdates.COLUMN_NAME_LONGITUDE);
                int timestamp = c.getColumnIndex(LocationUpdates.COLUMN_NAME_TIMESTAMP);
        
                int count = c.getCount();
                List<LocationUpdate> points = new ArrayList<LocationUpdate>(count);

                c.moveToFirst();
                // Loop through all Results
                do {
                    LocationUpdate point = new LocationUpdate(
                            c.getDouble(latIndex),
                            c.getDouble(longIndex),
                            c.getLong(timestamp));
                    points.add(point);
                    Log.d(Global.Company, point.toString());
                } while (c.moveToNext());
                return points;
            } else {
                Log.d(Global.Company, "Cursor is null");
                return null;
            }
        } finally {
            db.close();
        }
    }
    
    
}