package in.fastr.apps.stuck;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Jokes extends Activity{
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TextView textview = new TextView(this);
    textview.setText("Need to populate LIST");
    setContentView(textview);
  }

}
