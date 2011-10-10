package com.swblr;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.TextView;

public class GlobalContext {
  public LocationManager locationManager;
  // public StartButtonListener startButtonListener;
  public PissedButtonListener pissedButtonListener;
  public Button pissedButton;
  public String uuid;
  public Button startButton;
  public TextView displayText;
  public Context context;
  public Activity mainActivity;
 
}
