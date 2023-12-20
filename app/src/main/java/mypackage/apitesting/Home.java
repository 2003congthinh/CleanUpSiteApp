package mypackage.apitesting;
// Current location functions are from Week 5
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
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
    private String jsonString = "";
    private String name = "";
    private String loginAccount = HttpHandler.loginEmail;

    // Get cur loc
    Location cur_loc;
    protected FusedLocationProviderClient fusedLocationProviderClient;
    protected LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long UPDATE_INTERVAL = 20*1000 ;
    private static final long FASTEST_INTERVAL = 10*1000 ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Get cur loc
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();
        startLocationUpdate();


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
                intent.putExtra("latitude", cur_loc.getLatitude());
                intent.putExtra("longitude", cur_loc.getLongitude());
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
    private void requestPermission(){
        ActivityCompat.requestPermissions(Home.this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                cur_loc = locationResult.getLastLocation();
            }
        }, null);
    }
}