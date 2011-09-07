package in.fastr.apps.stuck;

import in.fastr.apps.common.Global;

import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.Context;
import android.app.Activity;


public class DbWrapper {
    private final String TableName = "congestionPoints";
    private static final String COL_LATITUDE = "Latitude";
    private static final String COL_LONGITUDE = "Longitude";
    private static final String COL_TIMESTAMP = "Timestamp";
    private static final String COL_STATUS = "Status";

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
    public void insertPoint(double latitude, double longitude) {
        Date d = new Date();
        long epochtime = d.getTime();

        Log.d(Global.Company, "Attempting write to SQL");

        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        try {
            String columns = " (Latitude, Longitude, Timestamp, Status) ";

            db.execSQL("INSERT INTO " + TableName + columns + "VALUES ("
                    + latitude + ", " 
                    + longitude + ", " 
                    + epochtime + ", " 
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
                    String coordinate = String.format("%d %d", lat, lon);
                    Log.d(Global.Company, coordinate);
                } while (c.moveToNext());
            }
        } finally {
            db.close();
        }
    }
    
    public int countRecordsForUpload() {
        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        try {
            final String SQL_STATEMENT = "SELECT COUNT(*) FROM " + TableName + 
                " WHERE Status='New'";
            
            Cursor c = db.rawQuery(SQL_STATEMENT, null);
            
            // TODO: THIS IS BUGGY
            int count = c.getInt(0);
            return count;
            // db.execSQL("SELECT COUNT(*) FROM " + TableName + " WHERE Status='New'");
        } finally {
            db.close();
        }
    }

    public Object getDbContext() {
        SQLiteDatabase db = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        return db;
    }

    public void closeDbContext(Object dbContext) {
        ((SQLiteDatabase) dbContext).close();
    }
    
    
    public void startUpload(Object dbContext) {
    }

    public void stopUpload(Object dbContext) {
        
    }
}