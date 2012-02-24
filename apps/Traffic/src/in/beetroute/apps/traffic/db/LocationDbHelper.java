package in.beetroute.apps.traffic.db;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.location.LocationUpdate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;


/**
 * Helper class for using the location updates DB.
 * It extends SQLiteOpenHelper giving access to the DB object
 * 
 * SQLite limits: http://stackoverflow.com/questions/4759907/dealing-with-a-large-database-in-android/
 * 
 * @author gauravlochan
 */
public class LocationDbHelper extends SQLiteOpenHelper {
    private static final String TAG = Global.COMPANY;
    private static final String dbName = "beetroute.db";
    private static final Integer dbVersion = 1;
    
    
    /**
     * Status of the record in the server.  
     * DO NOT REORDER OR CHANGE otherwise it will screw up how all the existing 
     * records are treated
     */
    private enum UploadStatus {
        NOT_UPLOADED,
        UPLOADED,
        UPLOADING     // TODO: Start to use this eventually
    };

    /**
     * A class that defines the table
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

    public LocationDbHelper(Context context, CursorFactory factory) {
        super(context, dbName, factory, dbVersion);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.debug(TAG, "Attempting to create DB " + dbName);

        /* Create a Table in the Database. */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LocationUpdates.TABLE_NAME +
                "(" + LocationUpdates.getSchema() + ");");

        Logger.debug(TAG, "Successfully created DB");
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.warn(TAG, "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + LocationUpdates.TABLE_NAME);
        onCreate(db);
    }    
    
    
    /**
     * Insert a single reported location update
     * @param uploaded TODO
     */
    public void insertPoint(LocationUpdate point, Boolean uploaded) {
        Logger.debug(TAG, "Attempting write point to DB " + dbName);
        SQLiteDatabase db = getWritableDatabase();
        
        Integer status = uploaded ? UploadStatus.UPLOADED.ordinal() :
                                   UploadStatus.NOT_UPLOADED.ordinal();

        try {
            db.execSQL("INSERT INTO " + LocationUpdates.TABLE_NAME + 
                    LocationUpdates.getColumns() + " VALUES ("
                    + point.getLatitude() + ", " 
                    + point.getLongitude() + ", " 
                    + point.getEpochTime() + ", " 
                    + point.getSpeed() + ", "
                    + status + ");"
                    );
            
            Logger.debug(TAG, "Succesfully inserted point into DB");
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
            int Column3 = c.getColumnIndex(LocationUpdates.COLUMN_NAME_TIMESTAMP);
            int Column4 = c.getColumnIndex(LocationUpdates.COLUMN_NAME_SPEED);
    
            if (c.moveToFirst()) {
                // Loop through all Results
                do {
                    double lat = c.getDouble(Column1);
                    double lon = c.getDouble(Column2);
                    long epochTime = c.getLong(Column3);
                    Date timeStamp = new Date(epochTime);
                    float speed = c.getFloat(Column4);
                    
                    String coordinate = String.format("%s %f %f %f", timeStamp.toLocaleString(), lat, lon, speed);
                    Logger.debug(TAG, coordinate);
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
                    + LocationUpdates.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NOT_UPLOADED.ordinal()
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
    public List<LocationUpdate> getUnsyncedLocationUpdates(Integer maxRecords) {
        SQLiteDatabase db = getReadableDatabase();
        
        try {
            String query = "SELECT * FROM " + LocationUpdates.TABLE_NAME + " WHERE " + 
                    LocationUpdates.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NOT_UPLOADED.ordinal() + ";";
            Cursor c = db.rawQuery(query, null);

            if (c != null) {
                int latIndex = c.getColumnIndex(LocationUpdates.COLUMN_NAME_LATITUDE);
                int longIndex = c.getColumnIndex(LocationUpdates.COLUMN_NAME_LONGITUDE);
                int timestamp = c.getColumnIndex(LocationUpdates.COLUMN_NAME_TIMESTAMP);
        
                int i = c.getCount();
                List<LocationUpdate> points = new ArrayList<LocationUpdate>(i);

                boolean recordsLeft = c.moveToFirst();
                int count = 0;

                Logger.debug(TAG, "Print all NOT_UPLOADED location updates");
                // Loop through all Results
                while (recordsLeft) {
                    // Break out if there was a record limit and we've exceeded it
                    if (maxRecords!=0 && count >= maxRecords) {
                        break;
                    } 
                    
                    LocationUpdate point = new LocationUpdate(
                            c.getDouble(latIndex),
                            c.getDouble(longIndex),
                            c.getLong(timestamp));
                    points.add(point);
                    count++;
                    Logger.debug(TAG, point.toString());
                } while (c.moveToNext());
                
                return points;
            } else {
                Logger.debug(TAG, "Cursor is null");
                return null;
            }
        } finally {
            db.close();
        }
    }
    
   
}