package in.beetroute.apps.findme;


import in.beetroute.apps.traffic.AppGlobal;
import in.beetroute.apps.traffic.MapPoint;
import in.beetroute.apps.traffic.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConfirmPlotRoute extends Activity {

    private MapPoint toAddress;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showdialog);
    }
	
    public void getRouteMap(View view) {
        if (view.getId() == R.id.button1) {
            // The previous activity should have passed in the destination
            // MapPoint.
            // Pass it along to the new activity
            Bundle extras = getIntent().getExtras();
            MapPoint dest = (MapPoint) extras.getSerializable(AppGlobal.LOCATION_FROM_SMS_KEY);
            Intent intent = new Intent(this, PlotRouteActivity.class);
            intent.putExtra(AppGlobal.LOCATION_FROM_SMS_KEY, dest);
            startActivity(intent);
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