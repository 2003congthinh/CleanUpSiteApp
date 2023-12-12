package mypackage.apitesting;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import mypackage.apitesting.databinding.JoinSiteBinding;

public class JoinSite extends FragmentActivity implements OnMapReadyCallback {
    String location = "";
    private GoogleMap mMap;
    private JoinSiteBinding binding;
    private String jsonString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = JoinSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Find location by name
        SearchView searchView = findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                location = searchView.getQuery().toString();
                new GetSearchSite().execute();
                Toast.makeText(JoinSite.this, "bahaha", Toast.LENGTH_SHORT).show();
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
        new JoinSite.GetAllSites().execute();
    }

    //  GET DATA
    private class GetAllSites extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            jsonString = HttpHandler.getSite();
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

                    // Custom InfoWindow
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null; // Use the default InfoWindow frame
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            // Inflate the custom InfoWindow layout
                            View view = getLayoutInflater().inflate(R.layout.custom_marker_window, null);

                            // Find views in the layout
                            TextView titleTextView = view.findViewById(R.id.infoWindowTitle);
                            TextView snippetTextView = view.findViewById(R.id.infoWindowSnippet);
                            Button button = view.findViewById(R.id.infoWindowButton);

                            // Set data to views
                            titleTextView.setText(marker.getTitle());
                            snippetTextView.setText(marker.getSnippet());

                            // Set a click listener for the button
                            button.setOnClickListener(v -> {
                                Intent intent = new Intent(JoinSite.this, Home.class);
                                startActivity(intent);
                                Toast.makeText(JoinSite.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                            });
                            return view;
                        }
                    });
                    mMap.addMarker(markerOptions).showInfoWindow();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private class GetSearchSite extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            jsonString = HttpHandler.getOneSite(location);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Double site_latitudes = jsonObject.getDouble("site_latitude");
                    Double site_longitudes = jsonObject.getDouble("site_longitude");

                    LatLng siteLatLng = new LatLng(site_latitudes, site_longitudes);
                    Log.d("Marker Coordinates", "Lat: " + site_latitudes + ", Lng: " + site_longitudes);

                    // Find site
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(siteLatLng, 10));
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