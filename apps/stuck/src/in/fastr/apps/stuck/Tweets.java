package in.fastr.apps.stuck;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Tweets extends Activity {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TextView textview = new TextView(this);
    textview.setText("Tweets near YOU");
    setContentView(textview);
  }

}
