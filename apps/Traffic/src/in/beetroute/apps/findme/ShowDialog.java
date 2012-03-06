package in.beetroute.apps.findme;


import in.beetroute.PlotRouteMap;
import in.beetroute.R;
import in.beetroute.R.id;
import in.beetroute.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShowDialog extends Activity {
	private static final int DIALOG_ALERT = 10;
	private String toAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showdialog);
		Bundle extras = getIntent().getExtras();
		toAddress = extras.getString("latlon");
		
		//showDialog(DIALOG_ALERT);
	}
	
	public void getRouteMap(View view) {
		if(view.getId() == R.id.button1) {
			try {
				Bundle extras = getIntent().getExtras();
				toAddress = extras.getString("latlon");
				Intent intent = new Intent(this, PlotRouteMap.class);
				intent.putExtra("latlon", toAddress);
				startActivity(intent);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void doNothing(View view) {
		if(view.getId() == R.id.button2) {
			this.finish();
		}
	}
}
	
/*
	protected Dialog onCreateDialog(int id) {
		
		String formatString = "Do you want directions for your friend";
		switch (id) {
		case DIALOG_ALERT:
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(formatString);
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new OkOnClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
		}
		return super.onCreateDialog(id);
	}
	
	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(getApplicationContext(), PlotRouteMap.class);
			startActivity(intent);
		}
	}

	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			ShowDialog.this.finish();
		}
	}	
}

*/