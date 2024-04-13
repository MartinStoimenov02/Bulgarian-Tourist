package com.example.turisticheska_knizhka.Activities;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.turisticheska_knizhka.Callbacks.PlacesCallback;
import com.example.turisticheska_knizhka.Callbacks.SingleNTO100Callback;
import com.example.turisticheska_knizhka.Callbacks.SinglePlaceCallback;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Helper;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Models.NTO100;
import com.example.turisticheska_knizhka.Models.Place;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class PlaceView extends AppCompatActivity {
    ImageView placeImageView;
    TextView placeNameTextView;
    TextView distanceTextView;
    TextView descriptionTextView;
    Button visitButton;
    Button showOnMapButton;
    ImageView flagImageView;
    ImageButton favouriteButton;
    ImageButton deleteButton;
    String email;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);

        String placeId = getIntent().getStringExtra("placeId");
        int caseNumber = getIntent().getIntExtra("caseNumber", 0);
        email = getIntent().getStringExtra("email");

        placeImageView = findViewById(R.id.placeImageView);
        placeNameTextView = findViewById(R.id.placeNameTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        visitButton = findViewById(R.id.visitButton);
        flagImageView = findViewById(R.id.flagImageView);
        favouriteButton = findViewById(R.id.favouriteButton);
        showOnMapButton = findViewById(R.id.showOnMapButton);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        deleteButton = findViewById(R.id.deleteButton);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        switch (caseNumber){
            case 1:
                showMyPlace(placeId, false);
                break;
            case 2:
                show100ntos(placeId);
                break;
            case 3:
                showMyPlace(placeId, true);
                break;
            default:
                //
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Set result to canceled and finish activity
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showMyPlace(String placeId, boolean isVisited){
        navigationMenu(R.id.action_my_places);
        if(isVisited)visitButton.setVisibility(View.INVISIBLE);
        else visitButton.setText("Посети");

        QueryLocator.getMyPlaceById(placeId, new SinglePlaceCallback() {
            @Override
            public void onPlaceLoaded(Place place) {
                if (place != null) {
                    refreshDistance(place);
                    // Display place details
                    placeNameTextView.setText(place.getName());
                    descriptionTextView.setText(place.getDescription());
                    if(Helper.checkIsNTO(place)){
                        flagImageView.setVisibility(View.VISIBLE);
                    } else {
                        flagImageView.setVisibility(View.GONE);
                    }
                    setHeartImg(place);
                    changeFavourite(place);
                    deletePlace(place);

                    // Example for loading image using Glide
                    Glide.with(PlaceView.this)
                            .load(place.getImgPath())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(placeImageView);

                    showOnMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Helper.showOnMap(PlaceView.this, place);
                        }
                    });

                    visitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get current location
                            visitPlace(place);
                            //refreshDistance(place, false);
                        }
                    });
                } else {
                    // Handle case where place is null
                    Toast.makeText(PlaceView.this, "Place not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Toast.makeText(PlaceView.this, "Failed to fetch place data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deletePlace(Place place){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDeleting(place);
            }
        });
    }

    private void acceptDeleting(Place place) {
        // Create a popup window with a message prompting the user to add the email to the local database
        // and two buttons (Yes and No)
        PopupWindow popupWindow = new PopupWindow(this);
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        popupWindow.setContentView(popupView);

        // Set text of the popup message
        TextView popupText = popupView.findViewById(R.id.popupText);
        popupText.setText("Сигурни ли сте, че искате да изтриете тази дестинация?");

        // Handle button clicks
        Button btnYes = popupView.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryLocator.deletePlace(place.getId());
                Navigation nav = new Navigation(email, PlaceView.this);
                nav.navigateToPlaceListView(1);
            }
        });

        Button btnNo = popupView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });

        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void changeFavourite(Place place){
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FAVORITE", "id: "+place.getId());
                Log.d("FAVORITE", "fav: "+place.getIsFavourite());
                // Toggle favourite status
                place.setIsFavourite(!place.getIsFavourite());
                QueryLocator.updateFavouriteStatus(place.getId(), place.getIsFavourite());
                setHeartImg(place);
            }
        });
    }

    private void setHeartImg(Place place){
        if (place.getIsFavourite()) {
            favouriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            favouriteButton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void show100ntos(String placeId){
        navigationMenu(R.id.action_nto100);
        Log.d("NTO100_ID", "id: "+placeId);
        visitButton.setText("Добави за посещение");
        QueryLocator.getNTO100ById(placeId, new SingleNTO100Callback() {
            @Override
            public void onNTOLoaded(NTO100 nto100) {
                // Handle the loaded NTO100 object
                if (nto100 != null) {
                    refreshDistance(nto100);
                    // Display place details
                    placeNameTextView.setText(nto100.getNumberInNationalList()+". "+nto100.getName());
                    String formatedDistance = formatDistance(nto100.getDistance());
                    distanceTextView.setText(formatedDistance);
                    descriptionTextView.setText(nto100.getDescription());
                    flagImageView.setVisibility(View.VISIBLE);
                    favouriteButton.setVisibility(View.INVISIBLE);

                    // Example for loading image using Glide
                    Glide.with(PlaceView.this)
                            .load(nto100.getImgPath())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(placeImageView);

                    //check if the nto already added to my places
                    QueryLocator.getAllMyPlaces(email, new PlacesCallback() {
                        @Override
                        public void onPlacesLoaded(List<Place> places) {
                            // Update the filteredPlaces list
                            List<Place> filteredPlaces = places;
                            boolean flag = false;
                            for(Place pl : filteredPlaces){
                                Log.d("EQUALS", "places: "+nto100.getName()+"; "+pl.getName());
                                if(pl.getName().equals(nto100.getName())){
                                    flag = true;
                                    break;
                                }
                            }
                            if(flag){
                                visitButton.setEnabled(false);
                                visitButton.setVisibility(View.INVISIBLE);
                            }else{
                                visitButton.setEnabled(true);
                                visitButton.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error
                        }
                    });

                    visitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Place place = Helper.createPlaceFromNTO(nto100.getName() , nto100.getUrlMap(), nto100.getImgPath(), nto100.getDistance(), email, nto100.getId(), nto100.getDescription());
                            QueryLocator.addANewPlace(place, nto100.getId());
                            Toast.makeText(PlaceView.this, "Мястото е добавено успешно!", Toast.LENGTH_LONG).show();
                            visitButton.setEnabled(false);
                            visitButton.setVisibility(View.INVISIBLE);
                        }
                    });

                    showOnMapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Helper.showOnMap(PlaceView.this, nto100);
                        }
                    });

                } else {
                    // Handle case where NTO100 is null
                    Toast.makeText(PlaceView.this, "NTO100 not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Toast.makeText(PlaceView.this, "Failed to fetch NTO100 data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDistance(int distance){
        int formatedDistance = 0;
        if(distance>=1000){
            formatedDistance = (distance/1000);
            return formatedDistance+" км.";
        }else{
            return distance+" м.";
        }
    }

    public void refreshDistance(Object obj) {
        if (!Helper.isLocationEnabled(PlaceView.this)) {
            distanceTextView.setText("Разстоянието не е изчислено!");
            Toast.makeText(PlaceView.this, "Включете локацията  и презаредете!", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog progressDialog = ProgressDialog.show(PlaceView.this, "Моля изчакайте", "Изчисляване на разстоянието...", true, false);

        // Create a handler to implement the timeout
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                distanceTextView.setText("Разстоянието не е изчислено!");
                Toast.makeText(PlaceView.this, "Включете локацията  и презаредете!", Toast.LENGTH_LONG).show();
            }
        }, 20000); // 20 seconds timeout

        Helper.getCurrentLocation(PlaceView.this, new com.example.turisticheska_knizhka.Callbacks.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                int distance = 0;
                if (location != null) {
                    if (obj instanceof Place) {
                        Place place = (Place) obj;
                        // Calculate distance between current location and destination
                        distance = (int) Helper.calculateDistance(place.getUrlMap(), location);
                        Log.d("DISTANCE", "dst: " + distance);
                        if (distance != -1) {
                            // Display distance
                            place.setDistance(distance);
                            progressDialog.dismiss();
                            QueryLocator.updatePlaceDistance(place, distance);
                        }
                    } else if (obj instanceof NTO100) {
                        NTO100 nto100 = (NTO100) obj;
                        // Calculate distance between current location and destination
                        distance = (int) Helper.calculateDistance(nto100.getUrlMap(), location);
                        Log.d("DISTANCE", "dst: " + distance);
                        if (distance != -1) {
                            // Display distance
                            nto100.setDistance(distance);
                            progressDialog.dismiss();
                            //QueryLocator.updatePlaceDistance(nto100, distance);
                        }
                    }
                }
                String formattedString = formatDistance(distance);
                distanceTextView.setText(formattedString);
                // Remove the timeout callback
                handler.removeCallbacksAndMessages(null);
            }
        });
    }


    private void navigationMenu(int nav){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(nav);
        Navigation navigation = new Navigation(email, PlaceView.this);
        navigation.bottomNavigation(bottomNavigationView);
    }

    private void visitPlace(Place place){
            if(place.getDistance()<500 && distanceTextView.getText()!="Разстоянието не е изчислено!"){
                place.setIsVisited(true);
                QueryLocator.updatePlaceVisitation(place);
                visitButton.setVisibility(View.INVISIBLE);
                Toast.makeText(PlaceView.this, "Успешно посетихте това място", Toast.LENGTH_LONG).show();
                Navigation nav = new Navigation(email, PlaceView.this);
                nav.navigateToPlaceListView(1);
            }else{
                Toast.makeText(PlaceView.this, "Трябва да сте на по-малко от 500 метра!", Toast.LENGTH_LONG).show();
            }
    }
}