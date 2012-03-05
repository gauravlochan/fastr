package in.beetroute.apps.traffic.db;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
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
        // This class cannot be instantiated
        private LocationTable() {}
        
        public static final String TABLE_NAME = "locationUpdates";

        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_TIMESTAMP = "Timestamp";
        public static final String COLUMN_NAME_SPEED = "Speed";
        public static final String COLUMN_NAME_UPLOAD_STATUS = "UploadStatus";
        
        // TODO: Need to add accuracy column
        
        public static final String COLUMN_NAMES_FOR_INSERT = String.format(" (%s, %s, %s, %s, %s) ",
                COLUMN_NAME_LATITUDE, COLUMN_NAME_LONGITUDE, COLUMN_NAME_TIMESTAMP,
                COLUMN_NAME_SPEED, COLUMN_NAME_UPLOAD_STATUS);

        public static String getSchema() {
            return _ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_NAME_LATITUDE + " Double, "
                    + COLUMN_NAME_LONGITUDE+ " Double, "
                    + COLUMN_NAME_TIMESTAMP+ " Double, "        // TODO: Why double?
                    + COLUMN_NAME_SPEED + " Double, "
                    + COLUMN_NAME_UPLOAD_STATUS + " Integer";
        }
        
        public static String getColumns() {
            return " (Latitude, Longitude, Timestamp, Speed, UploadStatus) ";
        }

    }

    
    public LocationDbHelper(Context context, CursorFactory factory) {
        super(context, AppGlobal.dbName, factory, AppGlobal.dbVersion);
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.debug(TAG, "Attempting to create table " + LocationTable.TABLE_NAME +
                " in DB " + AppGlobal.dbName);

        /* Create a Table in the Database. */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LocationTable.TABLE_NAME +
                "(" + LocationTable.getSchema() + ");");

        Logger.debug(TAG, "Successfully created table");
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.info(TAG, "Request to upgrade " + LocationTable.TABLE_NAME
                + " from version " + oldVersion 
                + " to " + newVersion + ", which doesn't do anything");
        
        // TODO: Need to come up with a good upgrade script

        // db.execSQL("DROP TABLE IF EXISTS " + LocationTable.TABLE_NAME);
        // onCreate(db);
    }   
    
    
    /**
     * Insert a single reported location update
     * @param uploaded TODO
     */
    public void insertPoint(LocationUpdate point, Boolean uploaded) {
        Logger.debug(TAG, "Attempting write point to DB " + AppGlobal.dbName);
        SQLiteDatabase db = getWritableDatabase();
        
        Integer status = uploaded ? UploadStatus.UPLOADED.ordinal() :
                                   UploadStatus.NOT_UPLOADED.ordinal();

        // TODO: Replace this with a db.insert
        try {
            db.execSQL("INSERT INTO " + LocationTable.TABLE_NAME 
                    + LocationTable.COLUMN_NAMES_FOR_INSERT
                    + " VALUES ("
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
            Cursor c = db.rawQuery("SELECT * FROM " + LocationTable.TABLE_NAME, null);
    
            int latIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_LATITUDE);
            int longIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_LONGITUDE);
            int timeIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_TIMESTAMP);
            int speedIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_SPEED);
    
            if (c.moveToFirst()) {
                // Loop through all Results
                do {
                    double lat = c.getDouble(latIndex);
                    double lon = c.getDouble(longIndex);
                    long epochTime = c.getLong(timeIndex);
                    Date timeStamp = new Date(epochTime);
                    float speed = c.getFloat(speedIndex);
                    
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
                    LocationTable.COLUMN_NAME_UPLOAD_STATUS + "=" + UploadStatus.NOT_UPLOADED.ordinal() + ";";
            Cursor c = db.rawQuery(query, null);

            if (c != null) {
                int latIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_LATITUDE);
                int longIndex = c.getColumnIndex(LocationTable.COLUMN_NAME_LONGITUDE);
                int timestamp = c.getColumnIndex(LocationTable.COLUMN_NAME_TIMESTAMP);
        
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
                            c.getLong(timestamp),
                            c.getDouble(latIndex),
                            c.getDouble(longIndex));
                    points.add(point);
                    count++;
                    Logger.debug(TAG, point.toString());
                    recordsLeft = c.moveToNext();
                }
                
                return points;
            } else {
                Logger.debug(TAG, "Cursor is null");
                return null;
            }
        } finally {
            db.close();
        }
    }
    
    /**
     * Gets all location updates after the specified timestamp
     * Close the cursor when done.
     * 
     * @param timestamp
     */
    public Cursor getNewerLocationUpdates(Long timestamp) {       
        SQLiteDatabase db = getReadableDatabase();
        
        // TODO: Currently not ordering it, since natural order should be fine
        // if this doesn't work, then order by timestamp
        
        // http://www.vogella.de/articles/AndroidSQLite/article.html#sqliteoverview_query
        return db.query(LocationTable.TABLE_NAME,
                new String[] { LocationTable._ID, 
                        LocationTable.COLUMN_NAME_LATITUDE,
                        LocationTable.COLUMN_NAME_LONGITUDE, 
                        LocationTable.COLUMN_NAME_SPEED,
                        LocationTable.COLUMN_NAME_TIMESTAMP },
                LocationTable.COLUMN_NAME_TIMESTAMP + ">?",     // where clause
                new String[] {timestamp.toString()},        // argument to where clause
                null, null, 
                LocationTable.COLUMN_NAME_TIMESTAMP);
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
                    new String[] { LocationTable._ID, 
                        LocationTable.COLUMN_NAME_LATITUDE,
                        LocationTable.COLUMN_NAME_LONGITUDE, 
                        LocationTable.COLUMN_NAME_SPEED,
                        LocationTable.COLUMN_NAME_TIMESTAMP
                        },
                    LocationTable._ID + "=?",               // where clause
                    new String[] {locationId.toString()},   // where parameter
                    null, null, null);
            
            if (c != null) {
                boolean recordsLeft = c.moveToFirst();
                if (!recordsLeft) {
                    return null;
                }
                return getCurrentLocationUpdate(c);
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
        
        LocationUpdate point = new LocationUpdate(
                c.getLong(timeIndex),
                c.getDouble(latIndex),
                c.getDouble(longIndex),
                c.getFloat(speedIndex),
                0f                          // TODO: Accuracy!!!
                );
        
        return point;
    }
}