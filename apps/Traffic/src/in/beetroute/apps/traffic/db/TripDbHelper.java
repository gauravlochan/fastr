package in.beetroute.apps.traffic.db;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.trip.Trip;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * 
 * From: http://www.vogella.de/articles/AndroidSQLite/article.html
 * "It is best practice to create a separate class per table. This class defines static
 *  onCreate() and onUpdate() methods. These methods are called in the corresponding 
 *  methods of SQLiteOpenHelper. This way your implementation of SQLiteOpenHelper will 
 *  stay readable, even if you have several tables."
 *  
 * @author gauravlochan
 *
 */
public class TripDbHelper extends SQLiteOpenHelper {
    private static final String TAG = Global.COMPANY;
    
    /**
     * A class that defines the table
     */
    public static final class TripTable implements BaseColumns {
        public static final String TABLE_NAME = "trips";

        public static final String COLUMN_NAME_START_POINT = "StartPoint";
        public static final String COLUMN_NAME_END_POINT = "EndPoint";
        public static final String COLUMN_NAME_START_NAME = "StartName";
        public static final String COLUMN_NAME_END_NAME = "EndName";
        
        // http://stackoverflow.com/questions/5289861/sqlite-android-foreign-key-syntax
        // http://www.sqlite.org/foreignkeys.html
        public static String getSchema() {
            return _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME_START_POINT + " INTEGER, "
                + COLUMN_NAME_END_POINT + " INTEGER, "
                + COLUMN_NAME_START_NAME + " TEXT, " 
                + COLUMN_NAME_END_NAME + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_NAME_START_POINT + ") REFERENCES " +
                LocationDbHelper.LocationTable.TABLE_NAME+ "(" + LocationDbHelper.LocationTable._ID + "), "
                + "FOREIGN KEY(" + COLUMN_NAME_END_POINT + ") REFERENCES " +
                LocationDbHelper.LocationTable.TABLE_NAME+ "(" + LocationDbHelper.LocationTable._ID + ")"
                ;
        }
        
        public static String[] getColumnsStringArray() {
            return new String[] { 
                    TripTable._ID,
                    TripTable.COLUMN_NAME_START_POINT,
                    TripTable.COLUMN_NAME_END_POINT,
                    TripTable.COLUMN_NAME_START_NAME,
                    TripTable.COLUMN_NAME_END_NAME
            };
        }


    }
        
    public TripDbHelper(Context context, CursorFactory factory) {
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

    
    public void deleteTable() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TripTable.TABLE_NAME);
            onCreate(db);
        } finally {
            db.close();
        }
    }

    
    /**
     * Create a trip
     * start point, end point, start name, end name
     */
    public void insertTrip(Trip trip) {
        Logger.debug(TAG, "Attempting write trip to DB");
        SQLiteDatabase db = getWritableDatabase();
        
        try {
            ContentValues values = new ContentValues(4);
            values.put(TripTable.COLUMN_NAME_START_POINT, trip.startPointId);
            values.put(TripTable.COLUMN_NAME_END_POINT, trip.endPointId);
            values.put(TripTable.COLUMN_NAME_START_NAME, trip.startPointName);
            values.put(TripTable.COLUMN_NAME_END_NAME, trip.endPointName);
            
            db.insertOrThrow(TripTable.TABLE_NAME, null, values);
            
            Logger.debug(TAG, "Succesfully inserted trip into DB");
        } finally {
            db.close();
        }
    }
    
    
    /**
     * Get trip by Id
     * @param tripId
     * @return
     */
    public Trip getTrip(Integer tripId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query(TripTable.TABLE_NAME,
                    TripTable.getColumnsStringArray(),
                    TripTable._ID + "=?",   // where clause
                    new String[] { tripId.toString() }, // where param
                    null, null, null);
            
            if (c != null) {
                if (!c.moveToFirst()) {
                    return null;
                }
                return getCurrentTrip(c);
            }
        } finally {
            db.close();
        }
        return null;
    }
    
    /**
     * Get the latest trip
     * @return
     */
    public Trip getLatestTrip() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query(TripTable.TABLE_NAME,
                    TripTable.getColumnsStringArray(),
                    null,   // where
                    null,   // where param
                    null,   // groupBy
                    null,   // having
                    TripTable._ID + " desc", // orderby
                    "1"     // limit
                    );
            if (c != null) {
                if (!c.moveToFirst()) {
                    return null;
                }
                return getCurrentTrip(c);
            }
        } finally {
            db.close();
        }
        return null;
    }
    

    /**
     * Cursor with all trips.  
     * Remember to close DB.
     * @return
     */
    public Cursor getAll() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TripTable.TABLE_NAME,
                    TripTable.getColumnsStringArray(),
                    null,   // where
                    null,   // where param
                    null,   // groupBy
                    null,   // having
                    null,
                    null);
    }

    
    /**
     * 
     * @param c
     * @return
     */
    private Trip getCurrentTrip(Cursor c) {
        int startPointIndex = c.getColumnIndex(TripTable.COLUMN_NAME_START_POINT);
        int endPointIndex = c.getColumnIndex(TripTable.COLUMN_NAME_END_POINT);
        int startNameIndex = c.getColumnIndex(TripTable.COLUMN_NAME_START_NAME);
        int endPointName = c.getColumnIndex(TripTable.COLUMN_NAME_END_NAME);
        
        return new Trip(
                c.getInt(startPointIndex),
                c.getInt(endPointIndex),
                c.getString(startNameIndex),
                c.getString(endPointName)
                );
     }
    
    
    
    /**
     * Print out the database contents
     */
    public void logDatabase() {
        SQLiteDatabase db = getWritableDatabase();

        // TODO: 
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TripTable.TABLE_NAME, null);
            // TODO: Print something!
        } finally {
            db.close();
        }
    }
        
}

