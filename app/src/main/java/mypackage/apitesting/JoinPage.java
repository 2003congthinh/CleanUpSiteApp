package mypackage.apitesting;
// Notification methods from Week 3
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JoinPage extends AppCompatActivity {
    private String getIntent = "";
    private String status = "";
    private String jsonString = "";
    private String name = "";
    private String owner = "";

    private String joinName = "";
    private int numbOfPeople;

    private NotificationManager notificationManager;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_page);

        getIntent = getIntent().getStringExtra("siteId");
        joinName = getIntent().getStringExtra("joinName");

        text = findViewById(R.id.text);

        // Call this method to create the notification channel
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels();
    }

    public void Join(View view) {
        new JoinedIn().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetSiteDetails().execute();
    }

    //    POST DATA
    private class JoinedIn extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.joinInSite(getIntent, joinName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (status.equals("Success: OK")) {
                Toast.makeText(JoinPage.this, status, Toast.LENGTH_SHORT).show();
                Notification notification = new NotificationCompat.Builder(JoinPage.this, "C.U.S")
                        .setSmallIcon(R.drawable.broom)
                        .setContentTitle("Join Site")
                        .setContentText("Congrats, you have joined " + owner + "'s " + "site: " + name + ".")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(1, notification);
            } else {
                Toast.makeText(JoinPage.this, "Something's wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetSiteDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            jsonString = HttpHandler.getSiteDetails(getIntent);
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void avoid){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    name = jsonObject.getString("name");
                    owner = jsonObject.getString("owner");
                }
                text.setText("owner: " + owner + "\n" + "name: " + name);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Create a notification channel
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    "C.U.S",
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            notificationManager.createNotificationChannel(channel1);
        }
    }

}