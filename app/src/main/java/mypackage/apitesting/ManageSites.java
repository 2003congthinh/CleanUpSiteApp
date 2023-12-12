package mypackage.apitesting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageSites extends AppCompatActivity {
    TextView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_sites);

        list = findViewById(R.id.siteList);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new FetchSitesForOwnerTask().execute();
    }

    private class FetchSitesForOwnerTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> siteInfoList = new ArrayList<>();

            try {
                // Make the HTTP request using HttpHandler
                String jsonResponse = HttpHandler.getMySite();

                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String siteName = jsonObject.getString("name");
                    String joinedPeople = jsonObject.getJSONArray("joined_people").toString();

                    // Create a string with site information
                    String siteInfo = "Site Name: " + siteName + "\nJoined People: " + joinedPeople;
                    siteInfoList.add(siteInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return siteInfoList;
        }

        @Override
        protected void onPostExecute(List<String> siteInfoList) {
            if (siteInfoList != null) {
                // Update the TextView with site information
                for (String siteInfo : siteInfoList) {
                    list.append(siteInfo + "\n\n");
                }
            } else {
                list.setText("Failed to fetch sites for owner");
            }
        }
    }

}