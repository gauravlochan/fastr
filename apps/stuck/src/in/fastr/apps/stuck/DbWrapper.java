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
    private Activity activity;
    
    public DbWrapper(Activity _activity) {
        activity = _activity;
    }
    
    /** 
     * Create a Database 
     */
    public void createDatabase() {
        SQLiteDatabase myDB = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        try {
            /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TableName
                    + " (Latitude Double, Longitude Double, Timestamp Double);");
        }
        catch (Exception e) {
            Log.d(Global.Company, "DB error", e);
        }
        finally {
            myDB.close();
        }
    }
    
    /**
     * Insert a single reported congestion point
     */
    public void insertPoint(double latitude, double longitude) {
        Date d = new Date();
        long epochtime = d.getTime();

        Log.d(Global.Company, "Attempting write to SQL");

        SQLiteDatabase myDB = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);
        try {
            // TODO: Need to handle the potential case where DB is full
            myDB.execSQL("INSERT INTO " + TableName
                    + " (Latitude, Longitude, Timestamp)" + " VALUES ("
                    + latitude + ", " + longitude + ", " + epochtime + ");");
    
            Log.d(Global.Company, "Succesful write to SQL");
        } finally {
            myDB.close();
        }
        
    }
    
    /**
     * Print out the database contents
     */
    public void logDatabase() {
        SQLiteDatabase myDB = 
            activity.openOrCreateDatabase("Traffix", Context.MODE_PRIVATE, null);

        try {
            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName, null);
    
            int Column1 = c.getColumnIndex("Latitude");
            int Column2 = c.getColumnIndex("Longitude");
    
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
            myDB.close();
        }
    }

}