package com.example.turisticheska_knizhka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaceListView extends AppCompatActivity {
    private static final int SORT_ALPHABETICAL = 0;
    private static final int SORT_ALPHABETICAL_WITH_PRIORITY = 1;
    private static final int SORT_ALPHABETICAL_WITH_FAVOURITES_FIRST = 2;
    String email;
    int caseNumber;
    QueryLocator queryLocator;
    ListView placeListView;
    private BottomNavigationView bottomNavigationView;
    List<Place> places;
    PlaceAdapter adapter;
    Button addButton;
    private int currentSortType = SORT_ALPHABETICAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list_view);
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            caseNumber = intent.getIntExtra("caseNumber", 0);
        }

        placeListView = findViewById(R.id.placeListView);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_my_places);
        BottomNavigationView topMenu = findViewById(R.id.top_menu);
        topMenu.setSelectedItemId(R.id.space);
        Navigation navigation = new Navigation(email, PlaceListView.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.topMenu(topMenu);

        addButton = findViewById(R.id.addButton);

//        // Set OnClickListener for the sort button
//        sortButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Increment currentSortType and wrap around if it exceeds the maximum value
//                currentSortType = (currentSortType + 1) % 3;
//
//                // Sort the places according to the current sort type
//                sortPlaces(currentSortType);  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//                // Update the ListView with the sorted places
//                adapter.notifyDataSetChanged();
//            }
//        });

        switch(caseNumber){
            case 1:
                listMyPlaces();
                break;
            case 2:
                listNto100();
                break;
            case 3:
                listVisitedPlaces();
                break;
            default:
                Toast.makeText(PlaceListView.this, "грешка!", Toast.LENGTH_SHORT).show();
        }
    }

    private void listMyPlaces() {
        QueryLocator.getMyPlaces(email, new PlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                // Update the filteredPlaces list
                List<Place> filteredPlaces = places;

                // Populate the ListView with filtered places
                PlaceAdapter adapter = new PlaceAdapter(PlaceListView.this, filteredPlaces, email, caseNumber);
                placeListView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Toast.makeText(PlaceListView.this, "Error loading places", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listNto100(){
        addButton.setVisibility(View.GONE);
        QueryLocator.getNto100(new NTO100Callback() {
            @Override
            public void onNTO100Loaded(List<NTO100> nto100s) {
                // Update the filteredPlaces list
                List<NTO100> allNTO100s = nto100s;

                // Populate the ListView with filtered places
                NTOAdapter adapter = new NTOAdapter(PlaceListView.this, allNTO100s);
                placeListView.setAdapter(adapter);

                placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get the clicked item
                        NTO100 clickedNTO100 = allNTO100s.get(position);

                        Log.d("NTO100", "id: "+clickedNTO100.getId());

                        // Start the PlaceView activity
                        Intent placeViewIntent = new Intent(PlaceListView.this, PlaceView.class);
                        placeViewIntent.putExtra("placeId", clickedNTO100.getId());
                        placeViewIntent.putExtra("caseNumber", caseNumber);
                        placeViewIntent.putExtra("email", email);
                        startActivity(placeViewIntent);
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Toast.makeText(PlaceListView.this, "Error loading places", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listVisitedPlaces(){
        // Имплементация на метода по ваше желание
    }

    private void sortPlaces(List<Place> places, int sortType) {
        switch (sortType) {
            case SORT_ALPHABETICAL:
                Collections.sort(places, new Comparator<Place>() {
                    @Override
                    public int compare(Place place1, Place place2) {
                        return place1.getName().compareToIgnoreCase(place2.getName());
                    }
                });
                break;
            case SORT_ALPHABETICAL_WITH_PRIORITY:
                Collections.sort(places, new Comparator<Place>() {
                    @Override
                    public int compare(Place place1, Place place2) {
                        // Places with isFavourite=true have higher priority
                        if (place1.getIsFavourite() && !place2.getIsFavourite()) {
                            return -1; // place1 comes before place2
                        } else if (!place1.getIsFavourite() && place2.getIsFavourite()) {
                            return 1; // place2 comes before place1
                        } else {
                            // If both are favourites or both aren't, sort alphabetically
                            return place1.getName().compareToIgnoreCase(place2.getName());
                        }
                    }
                });
                break;
            case SORT_ALPHABETICAL_WITH_FAVOURITES_FIRST:
                Collections.sort(places, new Comparator<Place>() {
                    @Override
                    public int compare(Place place1, Place place2) {
                        // Places with nto100 != null have higher priority
                        if (place1.getNto100() != null && place2.getNto100() == null) {
                            return -1; // place1 comes before place2
                        } else if (place1.getNto100() == null && place2.getNto100() != null) {
                            return 1; // place2 comes before place1
                        } else {
                            // If both have nto100 or both don't, sort alphabetically
                            return place1.getName().compareToIgnoreCase(place2.getName());
                        }
                    }
                });
                break;
        }
    }

}
