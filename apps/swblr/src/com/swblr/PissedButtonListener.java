package com.swblr;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PissedButtonListener implements OnClickListener {
	public Button p;
	private GlobalContext globals;
	
	public static final String CHILL = "Chill Maadi :)";
	public static final String PISSED = "Stuck in traffic!";
	
	public PissedButtonListener (GlobalContext globals) {
		this.globals = globals;
	}

	@Override
	public void onClick(View v) {
		String currentState = globals.pissedButton.getText().toString();
    	if (currentState.equals(PISSED) ) {
			globals.pissedButton.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
			globals.pissedButton.setText(CHILL);
    	} else {
    		globals.pissedButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
    		//globals.pissedButton.setText(PISSED);
    		globals.pissedButton.setText(PISSED);
    		
    	}
	}
}

