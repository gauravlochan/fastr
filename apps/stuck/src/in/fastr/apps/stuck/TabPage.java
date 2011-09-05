package in.fastr.apps.stuck;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TabPage extends TabActivity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.showsomething);

    Resources res = getResources(); // Resource object to get Drawables
    TabHost tabHost = getTabHost();  // The activity TabHost
    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
    Intent intent;  // Reusable Intent for each tab

    // Create an Intent to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, Jokes.class);

    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("jokes").setIndicator("LOL",
                      res.getDrawable(R.drawable.ic_tab_whenstuck))
                  .setContent(intent);
    tabHost.addTab(spec);

    // Do the same for the other tabs
    intent = new Intent().setClass(this, Tweets.class);
    spec = tabHost.newTabSpec("tweets").setIndicator("Tweets Near YOU",
                      res.getDrawable(R.drawable.ic_tab_whenstuck))
                  .setContent(intent);
    tabHost.addTab(spec);

    intent = new Intent().setClass(this, News.class);
    spec = tabHost.newTabSpec("news").setIndicator("News",
                      res.getDrawable(R.drawable.ic_tab_whenstuck))
                  .setContent(intent);
    tabHost.addTab(spec);

    tabHost.setCurrentTab(0);
}
}
