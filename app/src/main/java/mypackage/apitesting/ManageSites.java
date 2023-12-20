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
    public String homeName = "";
    TextView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_sites);

        homeName = getIntent().getStringExtra("ownerName");

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
                String jsonResponse = HttpHandler.getMySite(homeName);
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String siteName = jsonObject.getString("name");
                    JSONArray joinedPeopleArray = jsonObject.getJSONArray("joined_people");

                    StringBuilder siteInfoBuilder = new StringBuilder();
                    siteInfoBuilder.append("Site Name: ").append(siteName).append("\nJoined People:\n");

                    // Iterate over joinedPeopleArray and append each person to the string
                    for (int j = 0; j < joinedPeopleArray.length(); j++) {
                        String joinedPerson = joinedPeopleArray.getString(j);
                        siteInfoBuilder.append("- ").append(joinedPerson).append("\n");
                    }

                    siteInfoList.add(siteInfoBuilder.toString());
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