package mypackage.apitesting;
// Draw polylines functions are from Hmaza-Amir, git link: https://github.com/hamza-ameer/GoogleMaps-Find-Routes/blob/Updated/Google%20Maps%20Routes%20finding%20Guide.txt
// Create custome marker are from Week 5
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import mypackage.apitesting.databinding.JoinSiteBinding;

public class JoinSite extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private LatLng curLoc;
    private LatLng siteLatLng;
    private String location = "";
    private GoogleMap mMap;
    private JoinSiteBinding binding;
    private String jsonString = "";
    private String jsonStringSearch = "";

    // Get cur loc
    double curLatitude = 0.0;
    double curLongitude = 0.0;

    private String homeName = "";

    private Spinner numberFilter;

    //polyline object
    private List<Polyline> polylines=null;

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
                filterMarkers();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                            .icon(bitmapDescriptorFromVector(JoinSite.this, R.drawable.broom, 120, 120));

                    // Set the ID as a tag
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(siteId);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void filterMarkers() {
        // Get the selected items from the spinner
        String selectedNumber = numberFilter.getSelectedItem().toString();
        if (selectedNumber.equals("All")) {
            // Show all markers
            new GetAllSites().execute();
        } else {
            // Filter markers based on the selected number
            int minPeople, maxPeople;

            switch (selectedNumber) {
                case "0 to 5 people":
                    minPeople = 0;
                    maxPeople = 5;
                    break;
                case "5 to 10 people":
                    minPeople = 5;
                    maxPeople = 10;
                    break;
                case "More than 10 people":
                    minPeople = 10;
                    maxPeople = Integer.MAX_VALUE; // Assuming there is no upper limit
                    break;
                default:
                    return; // Invalid selection
            }

            // Clear the map
            mMap.clear();

            // show cur loc
            curLoc = new LatLng(curLatitude, curLongitude);
            MarkerOptions curOptions = new MarkerOptions()
                    .position(curLoc)
                    .title("My loc")
                    .icon(bitmapDescriptorFromVector(JoinSite.this, R.drawable.person, 100, 100));
            mMap.addMarker(curOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));

            // Add markers based on the filter criteria
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String names = jsonObject.getString("name");
                    Double siteLatitude = jsonObject.getDouble("site_latitude");
                    Double siteLongitude = jsonObject.getDouble("site_longitude");
                    String owners = jsonObject.getString("owner");
                    String siteId = jsonObject.getString("id");
                    int joinedPeopleCount = jsonObject.getJSONArray("joined_people").length();

                    // Check if the joinedPeopleCount is within the selected range
                    if (joinedPeopleCount >= minPeople && joinedPeopleCount <= maxPeople) {
                        LatLng siteLatLng = new LatLng(siteLatitude, siteLongitude);

                        // Add a marker for each site
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(siteLatLng)
                                .title(names)
                                .snippet("Owner: " + owners + "\nPeople Joined: " + joinedPeopleCount)
                                .icon(bitmapDescriptorFromVector(JoinSite.this, R.drawable.broom, 120, 120));

                        // Set the ID as a tag
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setTag(siteId);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private class GetSearchSite extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            jsonStringSearch = HttpHandler.getOneSite(location);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            try {
                JSONArray jsonArray = new JSONArray(jsonStringSearch);
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Double site_latitudes = jsonObject.getDouble("site_latitude");
                    Double site_longitudes = jsonObject.getDouble("site_longitude");

                    siteLatLng = new LatLng(site_latitudes, site_longitudes);
                    Log.d("Marker Coordinates", "Lat: " + site_latitudes + ", Lng: " + site_longitudes);

                    // Find site
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(siteLatLng, 15));
                    // Add polyline
                    Findroutes(curLoc, siteLatLng);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(JoinSite.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener((RoutingListener) JoinSite.this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyB_sIZ6bziC7B8OVm8o6AuCrEcJXFbSKk0")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(JoinSite.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines != null) {
            // Clear previous polylines
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.green));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                int k=polyline.getPoints().size();
                polylines.add(polyline);
            }
        }

    }

    @Override
    public void onRoutingCancelled() {
//        Findroutes(start,end);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId, int width, int height) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        curLoc = new LatLng(curLatitude, curLongitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(curLoc)
                .title("My loc")
                .icon(bitmapDescriptorFromVector(JoinSite.this, R.drawable.person, 100, 100));
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