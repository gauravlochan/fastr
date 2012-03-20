package in.beetroute.apps.findme;


import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;

public class ConfirmPlotRoute extends Activity {
    private static final int DIALOG_ALERT=10;
    private static final int PLOT_ROUTE_ACTIVITY=1;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showdialog);
		showDialog(DIALOG_ALERT);
	}
    
    @Override
   	protected void onStart() {
   		// TODO Auto-generated method stub
   		super.onStart();
   		FlurryAgent.onStartSession(this, "3K4UUTXNPWWT1GPGHC6L");
   	}


   	@Override
   	protected void onStop() {
   		// TODO Auto-generated method stub
   		super.onStop();
   		FlurryAgent.onEndSession(this);
   	}
		
   	@Override
	protected Dialog onCreateDialog(int id) {
		String fromPhoneNumber = getIntent().getExtras().getString(AppGlobal.SMS_PHONE_NUMBER);	
		String formatString = "Your friend " + fromPhoneNumber + 
		        " has sent their location information. Do you want to plot directions";
		switch (id) {
		case DIALOG_ALERT:
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.app_name);
			builder.setMessage(formatString);
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new OkOnClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Unroll the stack when someone comes back from the plotted route
		// Need to finish this activity, otherwise it stays like a black screen
		if(requestCode == PLOT_ROUTE_ACTIVITY) {
			if (resultCode == Activity.RESULT_CANCELED) {
				ConfirmPlotRoute.this.finish();
			}
		}
	}

	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
		    MapPoint fromAddress = (MapPoint) 
		            getIntent().getExtras().getSerializable(AppGlobal.LOCATION_FROM_SMS_KEY);

			Intent intent = new Intent(ConfirmPlotRoute.this, MainActivity.class);
			intent.putExtra(AppGlobal.LOCATION_FROM_SMS_KEY, fromAddress);
			startActivityForResult(intent, PLOT_ROUTE_ACTIVITY);
		}
	}

	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			ConfirmPlotRoute.this.finish();
			startActivity(new Intent(ConfirmPlotRoute.this, MainActivity.class));
		}
	}	
}
