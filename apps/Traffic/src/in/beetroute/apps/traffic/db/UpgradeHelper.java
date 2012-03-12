package in.beetroute.apps.traffic.db;

import in.beetroute.apps.commonlib.Global;
import in.beetroute.apps.commonlib.Logger;
import in.beetroute.apps.traffic.db.LocationDbHelper.LocationTable;
import in.beetroute.apps.traffic.db.TripDbHelper.TripTable;
import android.database.sqlite.SQLiteDatabase;

class UpgradeHelper {
    private static final String TAG = Global.COMPANY;

    public static Object lockObject = new Object();
    
    public static void onCreate(SQLiteDatabase db) {
        synchronized(lockObject) {
            Logger.debug(TAG, "Attempting to create table " + LocationTable.TABLE_NAME);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + LocationTable.TABLE_NAME +
                "(" + LocationTable.getSchema() + ");");

            Logger.debug(TAG, "Attempting to create table " + TripTable.TABLE_NAME);
            String sql = "CREATE TABLE IF NOT EXISTS " + TripTable.TABLE_NAME +
                    "(" + TripTable.getSchema() + ");";
            db.execSQL(sql);

            Logger.debug(TAG, "Successfully created tables");
        }
    }
    
    
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.info(TAG, "Request to upgrade DB from version " + oldVersion 
                + " to " + newVersion);
        
        switch (oldVersion) {
            case 1: 
                // We are now on version 2, so there the only older version is 1
                upgradev1tov2(db);
                break;
                
            default:
                Logger.warn(TAG, "Unknown DB upgrade path");
        }
    }    
    
    
    private static void upgradev1tov2(SQLiteDatabase db) {
        Logger.debug(TAG, "Attempting to create table " + TripTable.TABLE_NAME);
        String sql = "CREATE TABLE IF NOT EXISTS " + TripTable.TABLE_NAME +
                "(" + TripTable.getSchema() + ");";
        db.execSQL(sql);
        
        Logger.debug(TAG, "Add accuracy column to "+ LocationTable.TABLE_NAME);
        db.execSQL("ALTER TABLE " + LocationTable.TABLE_NAME + 
                " ADD COLUMN " + LocationTable.COLUMN_NAME_ACCURACY + " DOUBLE;");
        
    }

    
}
