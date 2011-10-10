package com.swblr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

// Perhaps this should all happen inside a context
// Copied from http://developer.android.com/reference/android/app/Service.html

public class GpsClient {
  private GpsService mBoundService;
  private boolean mIsBound;
  private Context context;
  
  GpsClient(Context context) {
    this.context = context;
  }

  public Location getLastLocation() {
    if (mIsBound) {
      return mBoundService.getLastLocation();
    } else {
      return null;
    }
  }

  private ServiceConnection mConnection = new ServiceConnection() {
      public void onServiceConnected(ComponentName className, IBinder service) {
          // This is called when the connection with the service has been
          // established, giving us the service object we can use to
          // interact with the service.  Because we have bound to a explicit
          // service that we know is running in our own process, we can
          // cast its IBinder to a concrete class and directly access it.
          mBoundService = ((GpsService.LocalBinder)service).getService();

          // Do something
      }

      public void onServiceDisconnected(ComponentName className) {
          // This is called when the connection with the service has been
          // unexpectedly disconnected -- that is, its process crashed.
          // Because it is running in our same process, we should never
          // see this happen.
          mBoundService = null;

          // Do something
      }
  };

  void doBindService() {
      // Establish a connection with the service.  We use an explicit
      // class name because we want a specific service implementation that
      // we know will be running in our own process (and thus won't be
      // supporting component replacement by other applications).
      context.bindService(new Intent(context, GpsService.class), 
          mConnection, Context.BIND_AUTO_CREATE);
      mIsBound = true;
  }

  void doUnbindService() {
      if (mIsBound) {
          // Detach our existing connection.
          context.unbindService(mConnection);
          mIsBound = false;
      }
  }

}

