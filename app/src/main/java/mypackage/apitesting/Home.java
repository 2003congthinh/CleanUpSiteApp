package mypackage.apitesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private String jsonString = "";
    private String name = "";
    private String loginAccount = HttpHandler.loginEmail;

    // Get cur loc
    private final int PERMISSION = 1;
    Location cur_loc;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Get cur loc
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        CardView CreateSite = findViewById(R.id.createSite);
        CardView ManageSites = findViewById(R.id.manageSites);
        CardView JoinSite = findViewById(R.id.joinSite);
        CardView JoinedSites = findViewById(R.id.joinedSites);
        CardView Logout = findViewById(R.id.logout);

        CreateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CreateSite.class);
                intent.putExtra("ownerName", name);
                startActivity(intent);
            }
        });

        ManageSites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ManageSites.class);
                intent.putExtra("ownerName", name);
                startActivity(intent);
            }
        });

        JoinSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_loc != null) {
                    Intent intent = new Intent(Home.this, JoinSite.class);
                    intent.putExtra("ownerName", name);
                    intent.putExtra("latitude", cur_loc.getLatitude());
                    intent.putExtra("longitude", cur_loc.getLongitude());
                    startActivity(intent);
                } else {
                    Toast.makeText(Home.this, "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        JoinedSites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, JoinedSites.class);
                intent.putExtra("joinerName", name);
                startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpHandler.loginEmail = "";
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        new Home.GetAccount().execute();
    }

    //  GET DATA
    private class GetAccount extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            jsonString = HttpHandler.getAccount(loginAccount);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    name = jsonObject.getString("name");
                }
                TextView Account = findViewById(R.id.account_text);
                if (!loginAccount.equals("")) {
                    Account.setText("Welcome to CleanUpSite, " + name);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        cur_loc = location;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(Home.this, "cur loc failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}