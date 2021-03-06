package in.beetroute.apps.traffic;

import greendroid.app.GDApplication;
import in.beetroute.apps.traffic.activities.MainActivity;

import org.acra.ACRA;

import android.content.Intent;
import android.net.Uri;

//@ReportsCrashes(formKey = "dHU2OFRVNXZrV1RBMl9neE1NSGZ6LUE6MQ")
public class TrafficApplication extends GDApplication {

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }
    
    @Override
    public Class<?> getHomeActivityClass() {
        return MainActivity.class;
    }
    
    @Override
    public Intent getMainApplicationIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.beetroute.in"));
    }

}
