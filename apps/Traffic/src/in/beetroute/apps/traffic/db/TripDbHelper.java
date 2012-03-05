package in.beetroute.apps.traffic.db;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.AppGlobal;
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
 * @author gauravlochan
 *
 */
public class TripDbHelper extends SQLiteOpenHelper {
    private static final String TAG = Global.COMPANY;
    
    /**
     * A class that defines the table
     */
    public static final class TripTable implements BaseColumns {
        // This class cannot be instantiated
        private TripTable() {}
        
        public static final String TABLE_NAME = "trips";

        public static final String COLUMN_NAME_START_POINT = "StartPoint";
        public static final String COLUMN_NAME_END_POINT = "EndPoint";
        public static final String COLUMN_NAME_START_NAME = "StartName";
        public static final String COLUMN_NAME_END_NAME = "EndName";
        
        public static final String COLUMN_STRING = String.format(" (%s, %s, %s, %s) ",
                COLUMN_NAME_START_POINT, COLUMN_NAME_END_POINT,
                COLUMN_NAME_START_NAME, COLUMN_NAME_END_NAME);

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

    }
        
    public TripDbHelper(Context context, CursorFactory factory) {
        super(context, AppGlobal.dbName, factory, AppGlobal.dbVersion);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.debug(TAG, "Attempting to create table in db");

        /* Create a Table in the Database. */
        String sql = "CREATE TABLE IF NOT EXISTS " + TripTable.TABLE_NAME +
                "(" + TripTable.getSchema() + ");";
        Logger.debug(TAG, sql);
        db.execSQL(sql);

        Logger.debug(TAG, "Successfully created table");
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.info(TAG, "Request to upgrade " + TripTable.TABLE_NAME
                + " from version " + oldVersion 
                + " to " + newVersion + ", which doesn't do anything");

        // TODO: Need to come up with a good upgrade script
        
        // db.execSQL("DROP TABLE IF EXISTS " + TripTable.TABLE_NAME);
        // onCreate(db);
    }
    
    
    /**
     * Create a trip
     * installation id, start point, end point, start name, end name
     */
    public void insertTrip() {
        
    }
    
    
    /**
     * Print out the database contents
     */
    public void logDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        
        Cursor c = db.rawQuery("SELECT * FROM " + TripTable.TABLE_NAME, null);
    }
    

}

