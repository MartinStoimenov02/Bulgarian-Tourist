package com.example.turisticheska_knizhka.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.turisticheska_knizhka.Callbacks.AddPlaceCallback;
import com.example.turisticheska_knizhka.Callbacks.NTO100Callback;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Models.NTO100;
import com.example.turisticheska_knizhka.Models.Place;
import com.example.turisticheska_knizhka.R;
import com.example.turisticheska_knizhka.databinding.ActivityAddPlaceBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private ActivityAddPlaceBinding binding;
    private String urlMap;
    EditText placeEditText;
    EditText placeDescription;
    private Button addButton;
    private ImageView searchButton;
    private static BottomNavigationView bottomNavigationView;
    InputMethodManager imm;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        placeEditText = findViewById(R.id.placeEditText);
        placeDescription = findViewById(R.id.placeDescription);
        addButton = findViewById(R.id.addButton);
        searchButton = findViewById(R.id.searchButton);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_my_places);
        Navigation navigation = new Navigation(email, AddPlaceActivity.this);
        navigation.bottomNavigation(bottomNavigationView);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urlMap==null){
                    Toast.makeText(AddPlaceActivity.this, "Моля изберете локация!", Toast.LENGTH_LONG).show();
                }else{
                    String placeName = String.valueOf(placeEditText.getText());
                    String description = String.valueOf(placeDescription.getText());
                    Log.d("PLACENAME", "place: "+placeName);
                    if(placeName==null || placeName.equals("")){
                        Toast.makeText(AddPlaceActivity.this, "Моля въведете име!", Toast.LENGTH_LONG).show();
                    } else{
                        ProgressDialog progressDialog = ProgressDialog.show(AddPlaceActivity.this, "Моля изчакайте", "Добавяне на дестинацията...", true, false);
                        getImage(urlMap, placeName, email, description);
                    }
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placeName = String.valueOf(placeEditText.getText());
                Log.d("PLACENAME", "place: "+placeName);
                if(placeName.equals("")){
                    Toast.makeText(AddPlaceActivity.this, "Моля въведете име!", Toast.LENGTH_LONG).show();
                } else{
                    getPlaceDetailsFromGoogle(placeName);
                }
            }
        });
    }

    /**
     * @param googleMap
     * description load the map on the screen and position at the curent location, or in the Sofia center
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a listener for map click events
        mMap.setOnMapClickListener(this);
        // Включване на показването на текущото местоположение на потребителя на картата
        // Проверка за разрешения за достъп до местоположението
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Заявяване на разрешения за достъп до местоположението
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Get current location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker at Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                    } else {
                        // If unable to get current location, set default location
                        LatLng defaultLocation = new LatLng(42.698334, 23.319941);
                        //mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Marker in Sofia"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f));
                    }
                });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        chooseLocationOnMap(latLng);
    }

    public void chooseLocationOnMap(LatLng latLng){
        mMap.clear(); // Clear existing markers
        mMap.addMarker(new MarkerOptions().position(latLng)); // Add a marker at the clicked location
        //mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Marker in Sofia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        // Construct URL based on coordinates
        urlMap = String.format(Locale.getDefault(), "https://maps.google.com/maps?q=%f,%f", latLng.latitude, latLng.longitude);

        // Display the URL
        Log.d("URL", urlMap);
    }

    public void getImage(String urlMap, String name, String email, String description) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the URL for the search request
                    URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" +
                            "AIzaSyBSrFIVWfPGscGFskb3s3tl1crSYL5lq9A" + "&cx=" + "8662ccaade9c34971" + "&searchType=image&q=" + name);

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Read the response
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder responseBuilder = new StringBuilder();
                    int data;
                    while ((data = inputStream.read()) != -1) {
                        responseBuilder.append((char) data);
                    }
                    inputStream.close();

                    Log.d("IMAGE", "run: "+responseBuilder);
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                    JSONArray items = jsonResponse.getJSONArray("items");

                    // Get a random image URL from the search results
                    Random random = new Random();
                    int randomIndex = random.nextInt(items.length());
                    String imageUrl = items.getJSONObject(randomIndex).getString("link");

                    Log.d("IMAGE", "Random Image URL: " + imageUrl);
                    addNewPlace(urlMap, name, email, imageUrl, description);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void addNewPlace(String urlMap, String name, String email, String imgPath, String description){
        QueryLocator.getNto100(new NTO100Callback() {
            @Override
            public void onNTO100Loaded(List<NTO100> nto100s) {
                DocumentReference ntoRef = null;
                // Update the filteredPlaces list
                List<NTO100> allNTO100s = nto100s;
                for(NTO100 nto100 : allNTO100s){
                    if(name.equals(nto100.getName())){
                        ntoRef = QueryLocator.getNTORef(nto100.getId());
                        break;
                    }
                }

                DocumentReference userRef = QueryLocator.getUserRef(email);

                Place newPlace = new Place(
                        name, urlMap, imgPath, userRef, ntoRef, description
                );

                //QueryLocator.addPlaceToMyPlaces(newPlace);
                QueryLocator.addPlaceToMyPlaces(newPlace, new AddPlaceCallback() {
                    @Override
                    public void onPlaceAdded(String placeId) {
                        if (placeId != null) {
                            navigateToNewPlace(placeId);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    private void getPlaceDetailsFromGoogle(String placeName) {
        Log.d("PLACEDETAILS", "getPlaceDetailsFromGoogle");
        // Construct the URL for the Google Places API request
        String apiKey = "AIzaSyD_GRf1KLUaX7r8cIiRGSNgkeklL5F0u2I";
        String url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json" +
                "?input=" + placeName +
                "&inputtype=textquery" +
                "&fields=all" +
                "&key=" + apiKey;

        // Make the API call using Volley, Retrofit, or any other networking library of your choice
        // For simplicity, I'll use Volley here
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONArray("candidates").getJSONObject(0);
                            JSONObject geometry = result.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");

                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");

                            Log.d("PLACEDETAILS", "Latitude: " + latitude + ", Longitude: " + longitude);
                            LatLng searchedLocation = new LatLng(latitude, longitude);
                            chooseLocationOnMap(searchedLocation);
                            //View view = getLayoutInflater().inflate(R.layout.activity_add_place, null);
                            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Add the request to the Volley request queue
        // Make sure you have Volley set up properly in your project
        Volley.newRequestQueue(this).add(request);
    }

    private void navigateToNewPlace(String placeId){
        Log.d("PLACEID", "pla:"+placeId);

        Intent placeViewIntent = new Intent(AddPlaceActivity.this, PlaceView.class);
        placeViewIntent.putExtra("caseNumber", 1);
        placeViewIntent.putExtra("placeId", placeId);
        placeViewIntent.putExtra("email", email);
        startActivity(placeViewIntent);
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back when the back arrow in the toolbar is clicked
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
