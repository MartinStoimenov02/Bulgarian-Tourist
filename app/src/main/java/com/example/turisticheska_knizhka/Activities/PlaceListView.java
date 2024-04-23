package com.example.turisticheska_knizhka.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.turisticheska_knizhka.Adapters.NTOAdapter;
import com.example.turisticheska_knizhka.Adapters.PlaceAdapter;
import com.example.turisticheska_knizhka.Callbacks.NTO100Callback;
import com.example.turisticheska_knizhka.Callbacks.PlacesCallback;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Helper;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Models.NTO100;
import com.example.turisticheska_knizhka.Models.Place;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlaceListView extends AppCompatActivity {
    private static final int SORT_ALPHABETICAL = 0;
    private static final int SORT_BY_NUMBER_IN_NATIONAL_LIST = 1;
    private static final int SORT_ALPHABETICAL_WITH_100_FIRST = 1;
    private static final int SORT_BY_DISTANCE = 3;
    private static final int SORT_ALPHABETICAL_WITH_FAVOURITES_FIRST = 2;

    String email;
    int caseNumber;
    QueryLocator queryLocator;
    ListView placeListView;
    private static BottomNavigationView bottomNavigationView;
    PlaceAdapter adapter;
    Button addButton;
    private int distance;
    Button sortButton;
    TextView loadingTextView;
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

        addButton = findViewById(R.id.addButton);
        loadingTextView = findViewById(R.id.loadingTextView);
        sortButton = findViewById(R.id.sortButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddPlaceActivity when addButton is clicked
                Intent addPlaceIntent = new Intent(PlaceListView.this, AddPlaceActivity.class);
                addPlaceIntent.putExtra("email", email);
                startActivity(addPlaceIntent);
            }
        });

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
            case 4:
                listNearest();
                break;
            default:
                Toast.makeText(PlaceListView.this, "грешка!", Toast.LENGTH_SHORT).show();
        }
    }

    private void listMyPlaces() {
        navigationMenu(R.id.action_my_places);
        QueryLocator.getMyUnvisitedPlaces(email, new PlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                // Update the filteredPlaces list
                List<Place> filteredPlaces = places;
                //sortPlaces(SORT_ALPHABETICAL, filteredPlaces);
                sortPlaces(SORT_ALPHABETICAL, filteredPlaces, null);
                handleSortButton(filteredPlaces, null, null);

                // Populate the ListView with filtered places
                adapter = new PlaceAdapter(PlaceListView.this, filteredPlaces, email, caseNumber);
                placeListView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    private void listNto100(){
        navigationMenu(R.id.action_nto100);
        addButton.setVisibility(View.GONE);
        QueryLocator.getNto100(new NTO100Callback() {
            @Override
            public void onNTO100Loaded(List<NTO100> nto100s) {
                // Update the filteredPlaces list
                List<NTO100> allNTO100s = nto100s;
                sortPlaces(SORT_ALPHABETICAL, null, allNTO100s);

                // Populate the ListView with filtered places
                NTOAdapter adapter = new NTOAdapter(PlaceListView.this, allNTO100s);
                placeListView.setAdapter(adapter);
                handleSortButton(null, allNTO100s, adapter);

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
            public void onError(Exception e) {}
        });
    }

    private void listVisitedPlaces(){
        navigationMenu(R.id.action_home);
        addButton.setVisibility(View.GONE);
        QueryLocator.getVisitedPlaces(email, new PlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                // Update the filteredPlaces list
                List<Place> visitedPlaces = places;
                sortPlaces(SORT_ALPHABETICAL, visitedPlaces, null);
                handleSortButton(visitedPlaces, null, null);

                // Populate the ListView with filtered places
                adapter = new PlaceAdapter(PlaceListView.this, visitedPlaces, email, caseNumber);
                placeListView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    private void listNearest() {
        // Show "loading..." text in a TextView
        loadingTextView.setVisibility(View.VISIBLE);
        loadingTextView.setText("Изчисляване...");
        addButton.setVisibility(View.GONE);

        navigationMenu(R.id.action_nearest);
        QueryLocator.getMyUnvisitedPlaces(email, new PlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                // Set a timeout for 10 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI if calculation takes more than 10 seconds
                        if (loadingTextView.getVisibility() == View.VISIBLE) {
                            loadingTextView.setText("Не успяхме да намерим места в близост...\nЛокацията на устройството е спряна или няма интернет връзка!");
                            loadingTextView.setVisibility(View.VISIBLE);
                            addButton.setVisibility(View.GONE);
                        }
                    }
                }, 10000);

                Map<String, Place> nearestPlacesMap = new HashMap<String, Place>();
                for (Place pl : places) {
                    Helper.getCurrentLocation(PlaceListView.this, new com.example.turisticheska_knizhka.Callbacks.LocationCallback() {
                        @Override
                        public void onLocationResult(Location location) {
                            if (location != null) {
                                distance = (int) Helper.calculateDistance(pl.getUrlMap(), location);
                                if (distance < 10000) {
                                    if (!nearestPlacesMap.containsKey(pl.getId())) {
                                        nearestPlacesMap.put(pl.getId(), pl);
                                    }
                                } else {
                                    if (nearestPlacesMap.containsKey(pl.getId())) {
                                        nearestPlacesMap.remove(pl.getId());
                                    }
                                }
                            }
                            // Update the list view after processing each place
                            updateListView(nearestPlacesMap.values());
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error, if any
                Log.e("Error", e.getMessage());
                // Update the list view with empty list if an error occurs
                updateListView(new ArrayList<Place>());
            }
        });
    }


    // Method to update the ListView with the nearest places
    private void updateListView(Collection<Place> nearestPlacesCol) {
        List<Place> nearestPlacesList = new ArrayList<>();
        nearestPlacesList.addAll(nearestPlacesCol);
        sortPlaces(SORT_ALPHABETICAL, nearestPlacesList, null);
        handleSortButton(nearestPlacesList, null, null);
        if(nearestPlacesList.size()==0){
            loadingTextView.setVisibility(View.VISIBLE);
            loadingTextView.setText("Няма места в близост!");
        }else{
            loadingTextView.setVisibility(View.GONE);
        }
        // Populate the ListView with filtered places
        adapter = new PlaceAdapter(PlaceListView.this, nearestPlacesList, email, caseNumber);
        placeListView.setAdapter(adapter);
    }

    private void handleSortButton(List<Place> unsortedPlaces, List<NTO100> nto100, NTOAdapter ntoAdapter){
        // Set OnClickListener for the sort button
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isLocationEnabled(PlaceListView.this)&&unsortedPlaces!=null) {
                    currentSortType = (currentSortType + 1) % 3;
                } else if (nto100!=null) {
                    currentSortType = (currentSortType + 1) % 2;
                } else{
                    currentSortType = (currentSortType + 1) % 4;
                }
                if(unsortedPlaces!=null){
                    sortPlaces(currentSortType, unsortedPlaces, null);
                    adapter.notifyDataSetChanged();
                }
                if(nto100!=null && ntoAdapter!=null){
                    sortPlaces(currentSortType, null, nto100);
                    ntoAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void sortPlaces(int sortType, List<Place> places, List<NTO100> nto100s) {
        if (places != null) {
            switch (sortType) {
                case SORT_ALPHABETICAL:
                    sortButton.setText("\uD83D\uDD24");
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place place1, Place place2) {
                            return place1.getName().compareToIgnoreCase(place2.getName());
                        }
                    });
                    break;
                case SORT_ALPHABETICAL_WITH_FAVOURITES_FIRST:
                    sortButton.setText("♥");
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
                case SORT_ALPHABETICAL_WITH_100_FIRST:
                    sortButton.setText("\uD83D\uDCAF");
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

                case SORT_BY_DISTANCE:
                    sortButton.setText("⏳");
                    placeListView.setVisibility(View.INVISIBLE);
                    loadingTextView.setVisibility(View.VISIBLE);
                    loadingTextView.setText("Моля изчакайте...\nИзчисляване на разстояния...");
                    sortByDistanceWithTimeout(places);
                    break;
            }
        } else if(nto100s!=null){
            Log.d("NTOSORT", "329: "+nto100s.toString());
            Log.d("NTOSORT", "330: "+currentSortType);

            switch (sortType) {
                case SORT_ALPHABETICAL:
                    sortButton.setText("\uD83D\uDD24");
                    Collections.sort(nto100s, new Comparator<NTO100>() {
                        @Override
                        public int compare(NTO100 nto1001, NTO100 nto1002) {
                            return nto1001.getName().compareToIgnoreCase(nto1002.getName());
                        }
                    });
                    break;
                case SORT_BY_NUMBER_IN_NATIONAL_LIST:
                    sortButton.setText("\uD83D\uDD22");
                    Collections.sort(nto100s, new Comparator<NTO100>() {
                        @Override
                        public int compare(NTO100 nto1001, NTO100 nto1002) {
                            // Parse the strings to integers for comparison
                            int number1 = Integer.parseInt(nto1001.getNumberInNationalList());
                            int number2 = Integer.parseInt(nto1002.getNumberInNationalList());
                            // Compare the parsed integers
                            return number1 - number2;
                        }
                    });
                    break;
            }
        }
    }

    private void sortByDistanceWithTimeout(List<Place> places) {
//        while (!Helper.isLocationEnabled(PlaceListView.this)) {
//            loadingTextView.setVisibility(View.VISIBLE);
//            loadingTextView.setText("Включете локацията на устройството!");
//        }
        // Create a map to store distances for each place
        Map<Place, Integer> distanceMap = new HashMap<>();

        // Set a flag to track whether sorting is completed within the timeout
        AtomicBoolean sortingCompleted = new AtomicBoolean(false);

        // Start a timer to handle the timeout
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if sorting is completed
                if (!sortingCompleted.get()) {
                    // Sorting is not completed within the timeout
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Handle timeout: Update UI accordingly
                            placeListView.setVisibility(View.VISIBLE);
                            loadingTextView.setVisibility(View.INVISIBLE);
                            sortPlaces(SORT_ALPHABETICAL, places, null);
                            Toast.makeText(PlaceListView.this, "Не може да сортира по локация!", Toast.LENGTH_SHORT).show();

                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }, 10000); // 10 seconds timeout

        // Iterate through the places and calculate distances asynchronously
        for (Place pl : places) {
            if (!sortingCompleted.get()) {
                Helper.getCurrentLocation(PlaceListView.this, new com.example.turisticheska_knizhka.Callbacks.LocationCallback() {
                    @Override
                    public void onLocationResult(Location location) {
                        Log.d("INFLOOP", "listNearest");
                        if (!sortingCompleted.get() && location != null) {
                            // Calculate distance for each place
                            int distance = (int) Helper.calculateDistance(pl.getUrlMap(), location);
                            Log.d("DISTANCE", "dst: " + distance);
                            // Store the distance in the map
                            distanceMap.put(pl, distance);

                            // If all distances have been calculated, sort the list
                            if (distanceMap.size() == places.size()) {
                                // Sort the list of places based on distances
                                Collections.sort(places, new Comparator<Place>() {
                                    @Override
                                    public int compare(Place place1, Place place2) {
                                        return distanceMap.get(place1) - distanceMap.get(place2);
                                    }
                                });

                                // Update the ListView with the sorted places
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sortButton.setText("\uD83D\uDCCD");
                                        placeListView.setVisibility(View.VISIBLE);
                                        loadingTextView.setVisibility(View.INVISIBLE);
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                // Set sortingCompleted flag to true once sorting is completed
                                sortingCompleted.set(true);
                            }
                        }
                    }
                });
            } else {
                // Sorting is completed, exit the loop
                break;
            }
        }
    }



    private void navigationMenu(int nav){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(nav);
        BottomNavigationView topMenu = findViewById(R.id.top_menu);
        topMenu.setSelectedItemId(R.id.space);
        Navigation navigation = new Navigation(email, PlaceListView.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.topMenu(topMenu);
    }

}
