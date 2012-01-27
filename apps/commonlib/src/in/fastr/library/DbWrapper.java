package in.fastr.library;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class DbWrapper {
    private final String TableName = "congestionPoints";
    private static final String COL_LATITUDE = "Latitude";
    private static final String COL_LONGITUDE = "Longitude";
    private static final String COL_TIMESTAMP = "Timestamp";
    private static final String COL_STATUS = "Status";

    // TODO: Change this so that it doesn't need the activity passed in
    // instead, use the android.database.sqlite.SQLiteDatabase class
    private Activity activity;
    
    public DbWrapper(Activity _activity) {
        activity = _activity;
    }
    
    
    /** 
     * Create a Database 
     */
    public void createDatabase() {
        Log.d(Global.Company, "Attempting to create DB");

        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        try {
            // Using this for debugging for schema changes
            // db.execSQL("DROP TABLE "+TableName);
            
            String Schema = 
                COL_LATITUDE + " Double, " +
                COL_LONGITUDE + " Double, " +
                COL_TIMESTAMP + " Double, " +
                COL_STATUS + " Text";
            
            /* Create a Table in the Database. */
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TableName + 
                    "(" + Schema + ");");

            Log.d(Global.Company, "Successfully created DB");
        }
        catch (Exception e) {
            Log.d(Global.Company, "DB error", e);
        }
        finally {
            db.close();
        }
    }

    
    /**
     * Insert a single reported congestion point
     */
    public void insertPoint(CongestionPoint point) {
        Log.d(Global.Company, "Attempting write point to SQL");
        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);

        try {
            String columns = " (Latitude, Longitude, Timestamp, Status) ";

            db.execSQL("INSERT INTO " + TableName + columns + "VALUES ("
                    + point.getLatitude() + ", " 
                    + point.getLongitude() + ", " 
                    + point.getEpochTime() + ", " 
                    + "'New'" + 
                    ");");
            
            Log.d(Global.Company, "Succesful write to SQL");
        } finally {
            db.close();
        }

    }
    
    
    /**
     * Print out the database contents
     */
    public void logDatabase() {
        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TableName, null);
    
            int Column1 = c.getColumnIndex(COL_LATITUDE);
            int Column2 = c.getColumnIndex(COL_LONGITUDE);
    
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
     * Return count of congestion points that have not been uploaded.
     * 
     * @return count of congestion points that have not been uploaded
     */
    public long countUnsyncedCongestionPoints() {
        SQLiteDatabase db = activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        
        try {
            SQLiteStatement stmt = db.compileStatement("SELECT COUNT(*) FROM " + 
                    TableName + " WHERE Status='New'");
            long count = stmt.simpleQueryForLong();
            return count;
        } finally {
            db.close();
        }
    }
    
    /**
     * Loads a list of unsynced congestion points from the database.  
     * User can specify a limit to the number of points to load (chunkSize)
     * 
     * @param chunkSize the max number of points to get.  0 implies there
     *        is no limit
     *        
     * @return
     */
    @Deprecated
    public List<CongestionPoint> getUnsyncedCongestionPoints(long chunkSize) {
        // TODO: SqlLite doesnt seem to support "SELECT TOP n"
        return null;
    }
    
    /**
     * Loads a list of all unsynced congestion points from the database.  
     *        
     * @return
     */
    public List<CongestionPoint> getUnsyncedCongestionPoints() {
        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        
        try {
            String query = "SELECT * FROM " + TableName + " WHERE " + 
                COL_STATUS + "=='New';";
            Cursor c = db.rawQuery(query, null);

            if (c != null) {
                int latIndex = c.getColumnIndex(COL_LATITUDE);
                int longIndex = c.getColumnIndex(COL_LONGITUDE);
                int timestamp = c.getColumnIndex(COL_TIMESTAMP);
        
                int count = c.getCount();
                List<CongestionPoint> points = new ArrayList<CongestionPoint>(count);

                c.moveToFirst();
                // Loop through all Results
                do {
                    CongestionPoint point = new CongestionPoint(
                            ServiceProviders.SIMULATOR, // TODO: Fix this, store provider in Db
                            "", // TODO: Fix this
                            TrafficStatus.UNSPECIFIED // TODO: Fix this
                            );
                    
                    point.setLocation(c.getDouble(latIndex), c.getDouble(longIndex));
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