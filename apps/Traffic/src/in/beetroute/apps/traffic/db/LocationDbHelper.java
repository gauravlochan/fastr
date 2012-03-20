package in.beetroute.apps.traffic.db;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.location.LocationUpdate;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
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
    public static final class LocationTable implements BaseColumns {
        public static final String TABLE_NAME = "locationUpdates";

        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_TIMESTAMP = "Timestamp";
        public static final String COLUMN_NAME_SPEED = "Speed";
        public static final String COLUMN_NAME_ACCURACY = "Accuracy";
        
        public static final String COLUMN_NAME_UPLOAD_STATUS = "UploadStatus";
        
         public static String getSchema() {
            return _ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_NAME_LATITUDE + " Double, "
                    + COLUMN_NAME_LONGITUDE+ " Double, "
                    + COLUMN_NAME_TIMESTAMP+ " Double, "        // TODO: Why double?
                    + COLUMN_NAME_SPEED + " Double, "
                    + COLUMN_NAME_UPLOAD_STATUS + " Integer, "
                    + COLUMN_NAME_ACCURACY + " Double";
        }
         
        public static String[] getColumnsStringArray() {
            return new String[] { 
                    LocationTable._ID, 
                    LocationTable.COLUMN_NAME_LATITUDE,
                    LocationTable.COLUMN_NAME_LONGITUDE, 
                    LocationTable.COLUMN_NAME_TIMESTAMP,
                    LocationTable.COLUMN_NAME_SPEED,
                    LocationTable.COLUMN_NAME_UPLOAD_STATUS,
                    LocationTable.COLUMN_NAME_ACCURACY
            };
        }
    }

    
    public LocationDbHelper(Context context, CursorFactory factory) {
        super(context, AppGlobal.dbName, factory, AppGlobal.dbVersion);
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        UpgradeHelper.onCreate(db);
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        UpgradeHelper.onUpgrade(db, oldVersion, newVersion);
    }   
    
    
    /**
     * Insert a single reported location update
     * @param uploaded TODO
     */
    public void insertPoint(LocationUpdate point, Boolean uploaded) {
        Logger.verbose(TAG, "Attempting write point to table " + LocationTable.TABLE_NAME);
        SQLiteDatabase db = getWritableDatabase();
        
        Integer status = uploaded ? UploadStatus.UPLOADED.ordinal() :
                                   UploadStatus.NOT_UPLOADED.ordinal();

        try {
            ContentValues values = new ContentValues(4);
            values.put(LocationTable.COLUMN_NAME_LATITUDE, point.getLatitude());
            values.put(LocationTable.COLUMN_NAME_LONGITUDE, point.getLongitude());
            values.put(LocationTable.COLUMN_NAME_TIMESTAMP, point.getEpochTime());
            values.put(LocationTable.COLUMN_NAME_SPEED, point.getSpeed());
            values.put(LocationTable.COLUMN_NAME_UPLOAD_STATUS, status);
            values.put(LocationTable.COLUMN_NAME_ACCURACY, point.getAccuracy());
            
            db.insertOrThrow(LocationTable.TABLE_NAME, null, values);
            
            Logger.verbose(TAG, "Succesfully inserted point into " + LocationTable.TABLE_NAME);
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
            Cursor c = db.rawQuery("SELECT * FROM " + LocationTable.TABLE_NAME, null);
            if (c.moveToFirst()) {
                // Loop through all Results
                do {
                    LocationUpdate point = getCurrentLocationUpdate(c);
                    Logger.debug(TAG, point.toString());
                } while (c.moveToNext());
            }
            c.close();
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
                    + LocationTable.TABLE_NAME + " WHERE "
                    + LocationTable.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NOT_UPLOADED.ordinal()
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
            String query = "SELECT * FROM " + LocationTable.TABLE_NAME + " WHERE " + 
                    LocationTable.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NOT_UPLOADED.ordinal();
            Cursor c = db.rawQuery(query, null);

            if (c != null) {
                int i = c.getCount();
                List<LocationUpdate> points = new ArrayList<LocationUpdate>(i);

                boolean recordsLeft = c.moveToFirst();
                int count = 0;

                Logger.verbose(TAG, "Print all NOT_UPLOADED location updates");
                // Loop through all Results
                while (recordsLeft) {
                    // Break out if there was a record limit and we've exceeded it
                    if (maxRecords!=0 && count >= maxRecords) {
                        break;
                    } 
                    
                    LocationUpdate point = getCurrentLocationUpdate(c);
                    points.add(point);
                    count++;
                    Logger.debug(TAG, point.toString());
                    recordsLeft = c.moveToNext();
                }
                
                c.close();
                return points;
            } else {
                Logger.verbose(TAG, "Cursor is null");
                return null;
            }
        } finally {
            db.close();
        }
    }
    
    /**
     * Gets all location updates after the specified timestamp
     * Caller should manage the cursor and close it when done.
     * 
     * @param timestamp
     */
    public Cursor getNewerLocationUpdates(Long timestamp) {       
        SQLiteDatabase db = getReadableDatabase();
        
        // http://www.vogella.de/articles/AndroidSQLite/article.html#sqliteoverview_query
        return db.query(LocationTable.TABLE_NAME,
                LocationTable.getColumnsStringArray(),
                LocationTable.COLUMN_NAME_TIMESTAMP + ">?",     // where clause
                new String[] {timestamp.toString()},        // argument to where clause
                null, null, 
                LocationTable.COLUMN_NAME_TIMESTAMP         // order by timestamp
                );
    }

    
    /**
     * Gets the location object for the given locationId.
     * 
     * @param locationId
     * @return
     */
    public LocationUpdate getLocationUpdate(Integer locationId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query(LocationTable.TABLE_NAME,
                    LocationTable.getColumnsStringArray(),
                    LocationTable._ID + "=?",               // where clause
                    new String[] {locationId.toString()},   // where parameter
                    null, null, null);
            
            if (c != null) {
                boolean recordsLeft = c.moveToFirst();
                if (!recordsLeft) {
                    c.close();
                    return null;
                }
                LocationUpdate location = getCurrentLocationUpdate(c);
                c.close();
                return location;
            }
        } finally {
            db.close();
        }

        return null;
    }
    
    
    /**
     * Get the newest locationUpdate (TODO: UNTESTED)
     * 
     * @param locationId
     * @return
     */
    public LocationUpdate getNewestLocationUpdate() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query(LocationTable.TABLE_NAME,
                    LocationTable.getColumnsStringArray(),
                    LocationTable._ID + "=?",               // where clause
                    new String[] {"(select max("+LocationTable._ID+") from "+LocationTable.TABLE_NAME+")"},
                    null, null, null);
            
            if (c != null) {
                boolean recordsLeft = c.moveToFirst();
                if (!recordsLeft) {
                    c.close();
                    return null;
                }
                LocationUpdate location = getCurrentLocationUpdate(c);
                c.close();
                return location;
            }
        } finally {
            db.close();
        }

        return null;

    }

    /**
     * Gets the locationUpdate from the cursors current position.
     * Doesn't move the cursor
     * 
     * @param c
     * @return
     */
    public LocationUpdate getCurrentLocationUpdate(Cursor c) {
        int latIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_LATITUDE);
        int longIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_LONGITUDE);
        int timeIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_TIMESTAMP);
        int speedIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_SPEED);
        int accuracyIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_ACCURACY);

        LocationUpdate point = new LocationUpdate(
                c.getLong(timeIndex),
                c.getDouble(latIndex),
                c.getDouble(longIndex),
                c.getFloat(speedIndex),
                c.getFloat(accuracyIndex)
                );
        
        return point;
    }
}