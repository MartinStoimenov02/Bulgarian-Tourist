package com.example.turisticheska_knizhka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class PlaceView extends AppCompatActivity {
    ImageView placeImageView;
    TextView placeNameTextView;
    TextView phoneNumberTextView;
    TextView workingHoursTextView;
    TextView distanceTextView;
    Button visitButton;
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
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        workingHoursTextView = findViewById(R.id.workingHoursTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        visitButton = findViewById(R.id.visitButton);

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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        Navigation navigation = new Navigation(email, PlaceView.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.bottomNavigation(bottomNavigationView);

        switch (caseNumber){
            case 1:
                showMyPlace(placeId);
                break;
            case 2:
                show100ntos(placeId);
                break;
            case 3:
                //visited place with only sohw on map
                break;
            default:
                //
        }
    }

    private void showMyPlace(String placeId){
        visitButton.setText("Посети");
        QueryLocator.getMyPlaceById(placeId, new SinglePlaceCallback() {
            @Override
            public void onPlaceLoaded(Place place) {
                if (place != null) {
                    // Display place details
                    placeNameTextView.setText(place.getName());
                    phoneNumberTextView.setText(place.getPlacePhoneNumber());
                    workingHoursTextView.setText(place.getWorkingHours());
                    distanceTextView.setText(String.valueOf(place.getDistance()));

                    // Example for loading image using Glide
                    Glide.with(PlaceView.this)
                            .load(place.getImgPath())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(placeImageView);
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

    private void show100ntos(String placeId){
        Log.d("NTO100_ID", "id: "+placeId);
        visitButton.setText("Добави за посещение");
        QueryLocator.getNTO100ById(placeId, new SingleNTO100Callback() {
            @Override
            public void onNTOLoaded(NTO100 nto100) {
                // Handle the loaded NTO100 object
                if (nto100 != null) {
                    // Display place details
                    placeNameTextView.setText(nto100.getNumberInNationalList()+". "+nto100.getName());
                    phoneNumberTextView.setText(nto100.getPlacePhoneNumber());
                    workingHoursTextView.setText(nto100.getWorkingHours());
                    distanceTextView.setText(String.valueOf(nto100.getDistance()));

                    // Example for loading image using Glide
                    Glide.with(PlaceView.this)
                            .load(nto100.getImgPath())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(placeImageView);

                    //check if the nto already added to my places
                    QueryLocator.getMyPlaces(email, new PlacesCallback() {
                        @Override
                        public void onPlacesLoaded(List<Place> places) {
                            // Update the filteredPlaces list
                            List<Place> filteredPlaces = places;
                            boolean flag = false;
                            for(Place pl : filteredPlaces){
                                Log.d("EQUALS", "places: "+nto100.getName()+"; "+pl.getName());
                                if(pl.getName().equals(nto100.getName())){
                                    //if(pl.getName().equals(nto100.getName()) || pl.getId().equals(nto100.getId()) || pl.getUrlMap().equals(nto100.getUrlMap())){
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
                            Place place = Helper.createPlaceFromNTO(nto100.getName() , nto100.getUrlMap(), nto100.getWorkingHours(), nto100.getPlacePhoneNumber(), nto100.getImgPath(), nto100.getDistance(), email, nto100.getId());
                            QueryLocator.addANewPlace(place, nto100.getId());
                            visitButton.setEnabled(false);
                            visitButton.setVisibility(View.INVISIBLE);
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
}