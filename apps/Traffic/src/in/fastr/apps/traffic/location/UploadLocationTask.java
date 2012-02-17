package in.fastr.apps.traffic.location;

import in.fastr.apps.traffic.backend.ParseHelper;
import android.location.Location;
import android.os.AsyncTask;

import com.parse.ParseException;

/**
 * Upload task
 * Takes in the location to upload
 * No progress update method
 * If the upload fails, returns an exception
 * 
 * @author gauravlochan
 */
public class UploadLocationTask extends AsyncTask<Location, Void, Exception> {
    private String installationId;
    public UploadLocationTask(String _installationId) {
        installationId = _installationId;
    }

    @Override
    protected Exception doInBackground(Location... params) {
        try {
            ParseHelper.locationUpdate(params[0], installationId);
        } catch (ParseException e) {
            return e;
        }
        return null;
    }
 
    // Read http://developer.android.com/reference/android/os/AsyncTask.html#execute(Params...)
    @Override
    protected void onPostExecute(Exception result) {
        if (result == null) {
            // Successful write.  Mark as such in the DB
            
        } else {
            // Failed upload, don't do anything
        }
    }
        
}
