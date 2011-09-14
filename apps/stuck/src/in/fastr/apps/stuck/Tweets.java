package in.fastr.apps.stuck;

import in.fastr.apps.common.Global;
import in.fastr.apps.common.RESTHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Tweets extends Activity {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TextView textview = new TextView(this);
    String twitterUrl = 
      //"http://search.twitter.com/search.json?geocode=12.9132%2C77.6431%2C30mi";
        "http://twitter.com/statuses/user_timeline/GuyKawasaki.json";
    String twitterJson = RESTHelper.simpleGet(twitterUrl);
    String tweets = getTextFromJson(twitterJson);
    
    textview.setText(tweets);
    setContentView(textview);
    
    // http://search.twitter.com/search.json?geocode=12.9132%2C77.6431%2C30mi
  }

  public String getTextFromJson(String json) {
      StringBuilder builder = new StringBuilder();

      try {
          JSONArray jsonArray = new JSONArray(json);
          Log.i(App.Name, "Number of entries " + jsonArray.length());
          for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jsonObject = jsonArray.getJSONObject(i);
              String jsonText = jsonObject.getString("text");
              Log.i(App.Name, jsonText);
              builder.append(jsonText);
              builder.append("\n");

          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      return builder.toString();
  }

}
