package mypackage.apitesting;

import android.Manifest;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import mypackage.apitesting.databinding.JoinSiteBinding;

public class JoinSite extends FragmentActivity implements OnMapReadyCallback {
    private String location = "";
    private GoogleMap mMap;
    private JoinSiteBinding binding;
    private String jsonString = "";

    // Get cur loc
    double curLatitude = 0.0;
    double curLongitude = 0.0;

    private String homeName = "";

    private Spinner numberFilter;

    // Store the Polyline object to manage and remove it later
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = JoinSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get cur loc
        Intent intent = getIntent();
        curLatitude = intent.getDoubleExtra("latitude", 0.0);
        curLongitude = intent.getDoubleExtra("longitude", 0.0);
        homeName = intent.getStringExtra("ownerName");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Find location by name
        SearchView searchView = findViewById(R.id.search);

        // Get references to filter spinners
        numberFilter = findViewById(R.id.filterDropdown);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.number_array,
                android.R.layout.simple_spinner_item
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberFilter.setAdapter(adapter1);

        // Set up the listeners for the spinners
        numberFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //filterCardViews();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
            }
        });

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

//    public void Delete(View view) {
//        mMap.clear();
//    }

    @Override
    protected void onResume(){
        super.onResume();
        new GetAllSites().execute();
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
                    String siteId = jsonObject.getString("id");

                    LatLng siteLatLng = new LatLng(site_latitudes, site_longitudes);
                    Log.d("Marker Coordinates", "Lat: " + site_latitudes + ", Lng: " + site_longitudes);

                    // Add a marker for each site
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(siteLatLng)
                            .title(names)
                            .snippet("Owner: " + owners)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.broom)));

                    // Set the ID as a tag
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(siteId);
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

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
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
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // show cur loc
        LatLng curLoc = new LatLng(curLatitude, curLongitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(curLoc)
                .title("My loc")
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.person)));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));

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

                // Set data to views
                titleTextView.setText(marker.getTitle());
                snippetTextView.setText(marker.getSnippet());

                return view;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Handle the info window click here
                String siteId = (String) marker.getTag();
                Intent intent = new Intent(JoinSite.this, JoinPage.class);
                intent.putExtra("siteId", siteId);
                intent.putExtra("joinName", homeName);
                startActivity(intent);
                Toast.makeText(JoinSite.this, "Site ID: " + siteId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}