package in.fastr.apps.traffic;

import in.fastr.apps.traffic.activities.MainActivity;
import android.content.Intent;
import android.net.Uri;

import greendroid.app.GDApplication;

public class TrafficApplication extends GDApplication {

    @Override
    public Class<?> getHomeActivityClass() {
        return MainActivity.class;
    }
    
    @Override
    public Intent getMainApplicationIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fastr.in"));
    }

}
