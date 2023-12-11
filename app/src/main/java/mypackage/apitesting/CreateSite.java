package mypackage.apitesting;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mypackage.apitesting.databinding.CreateSiteBinding;

public class CreateSite extends FragmentActivity implements OnMapReadyCallback {

    private String siteName = null;
    private String locationName = null;
    private double siteLatitude;
    private double siteLongitude;

    private GoogleMap mMap;
    private CreateSiteBinding binding;

    private String status = "";
    private String jsonString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CreateSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(CreateSite.this);

        //Set visible
        CardView setVisible = findViewById(R.id.visible);
        CardView createSite = findViewById(R.id.createSite);
        ImageView backBtn = findViewById(R.id.backBtn);

        setVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSite.setVisibility(View.VISIBLE);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSite.setVisibility(View.INVISIBLE);
            }
        });

        //Find location by name
        SearchView searchView = findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geo = new Geocoder(CreateSite.this);
                    try {
                        addressList = geo.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && !addressList.isEmpty()) {
                        mMap.clear();

                        Address address = addressList.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();
                        String text = "Latitude: " + latitude + ", " + "Longitude: " + longitude;
                        LatLng latlng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(latlng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));
                        Toast.makeText(CreateSite.this, text, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        new GetSites().execute();
    }

    public void registerSite(View view){
        TextView name = findViewById(R.id.siteName);
        siteName = name.getText().toString();
        TextView locName = findViewById(R.id.locationName);
        locationName = locName.getText().toString();
        if(locationName != null && siteLatitude == 0.0 && siteLongitude == 0.0){
            textToLatLng();
        } else if (locationName == null && siteLatitude != 0.0 && siteLongitude != 0.0) {
            TextView locLat = findViewById(R.id.locationLatitude);
            String siteLat = locLat.getText().toString();
            TextView locLng = findViewById(R.id.locationLongitude);
            String siteLng = locLng.getText().toString();
            try {
                siteLatitude = Double.parseDouble(siteLat);
                siteLongitude = Double.parseDouble(siteLng);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new PostSite().execute();
    }
    public void textToLatLng(){
        List<Address> addressList = null;
        Geocoder geo = new Geocoder(CreateSite.this);
        try {
            addressList = geo.getFromLocationName(locationName, 1);
        } catch (IOException e){
            e.printStackTrace();
        }
        if (addressList != null && !addressList.isEmpty()) {
            Address address = addressList.get(0);
            siteLatitude = address.getLatitude();
            siteLongitude = address.getLongitude();
        }
    }


    //  POST DATA
    private class PostSite extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.postRequestCreateSite(siteName,siteLatitude,siteLongitude);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("Create site", "Status: " + status);
            if(status.equals("Success: OK")){
                Toast.makeText(CreateSite.this, status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CreateSite.this, "Something's wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //  GET DATA
    private class GetSites extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            jsonString = HttpHandler.getMySite();
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String names = jsonObject.getString("name");
                    Double site_latitudes = jsonObject.getDouble("site_latitude");
                    Double site_longitudes = jsonObject.getDouble("site_longitude");
                    String owners = jsonObject.getString("owner");

                    LatLng siteLatLng = new LatLng(site_latitudes, site_longitudes);
                    Log.d("Marker Coordinates", "Lat: " + site_latitudes + ", Lng: " + site_longitudes);

                    // Add a marker for each site
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(siteLatLng)
                            .title(names)
                            .snippet("Owner: " + owners);

                    mMap.addMarker(markerOptions);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

}