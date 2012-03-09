package in.beetroute.apps.findme;


import in.beetroute.apps.traffic.R;
import in.beetroute.apps.traffic.activities.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConfirmPlotRoute extends Activity {

    private String toAddress;
    private static final int DIALOG_ALERT=10;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showdialog);
		Bundle extras = getIntent().getExtras();
		toAddress = extras.getString("latlon");	
		showDialog(DIALOG_ALERT);
	}
	
	protected Dialog onCreateDialog(int id) {
		
		String formatString = "Your friend has sent their location information. Do you want to plot directions";
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
	
	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Bundle extras = getIntent().getExtras();
			toAddress = extras.getString("latlon");
			Intent intent = new Intent(ConfirmPlotRoute.this, PlotRouteActivity.class);
			intent.putExtra("latlon", toAddress);
			startActivity(intent);
		}
	}

	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			ConfirmPlotRoute.this.finish();
			startActivity(new Intent(ConfirmPlotRoute.this, MainActivity.class));
		}
	}	
}
