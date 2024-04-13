package com.example.turisticheska_knizhka.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.turisticheska_knizhka.Callbacks.PlacesCallback;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Models.Place;
import com.example.turisticheska_knizhka.Models.User;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1001;
    private static final int REQUEST_CODE_PLACE_LIST = 1;
    private String email;
    private FirebaseFirestore db;
    private List<Place> placeList;
    private TextView textViewPoints;
    private TextView textViewLevel;
    private TextView meTextView;
    private CardView secondCardView;
    private LinearLayout topUsersLayout;
    private BottomNavigationView bottomNavigationView;
    private TextView textViewEmailValue;
    private Button showVisitedPlaces;
    private int points;
    private int level;
    List<User> topUsers;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the email from the intent
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ако разрешението не е предоставено, поискайте го от потребителя
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        topUsers = new ArrayList<>();
        placeList = new ArrayList<>();

        textViewPoints = findViewById(R.id.textViewPointsNumber);
        textViewLevel = findViewById(R.id.textViewLevelNumber);
        showVisitedPlaces = findViewById(R.id.buttonListActivity);
        secondCardView = findViewById(R.id.secondCardView);
        topUsersLayout = findViewById(R.id.topUsersLayout);
        meTextView = findViewById(R.id.meTextView);

        textViewEmailValue = findViewById(R.id.textViewEmailValue);
        textViewEmailValue.setText("Вие сте влезнали като: " + email);

        showVisitedPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(HomeActivity.this, "Моля изчакайте", "Зареждане на списък...", true, false);
                Intent intent = new Intent(HomeActivity.this, PlaceListView.class);
                intent.putExtra("email", email);
                intent.putExtra("caseNumber", 3);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_PLACE_LIST);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        BottomNavigationView topMenu = findViewById(R.id.top_menu);
        topMenu.setSelectedItemId(R.id.space);
        Navigation navigation = new Navigation(email, HomeActivity.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.topMenu(topMenu);
        getLinkedVisitedPlaces();
        fetchTopUsers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // Проверете дали разрешението е предоставено
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ако разрешението е предоставено, продължете с нормалното изпълнение, например, извиквайки метода showMyPlace()
                // showMyPlace(placeId, false);
            } else {
                // Ако разрешението е отказано, обработете случая тук, например, покажете съобщение до потребителя или предоставете алтернативни опции
                Toast.makeText(HomeActivity.this, "Изчисляването на разстояние е невъзможно!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLACE_LIST) {
            // Dismiss the progress dialog here
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void displayPointsAndLevel() {
        textViewPoints.setText(String.valueOf(points)); // Convert int to String
        textViewLevel.setText(String.valueOf(level)); // Convert int to String
    }

    private void getLinkedVisitedPlaces() {
        db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(email);
        QueryLocator.getVisitedPlaces(email, new PlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                placeList = places;
                Log.d("PLACES", "places: "+placeList);
                displayCountOfPlaces();
                calculatePoints();
                calculateLevel();
                displayPointsAndLevel();
                updatePointsForUser(userRef);
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    private void updatePointsForUser(DocumentReference userRef) {
        // Update the points field for the user
        userRef.update("points", points)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "Points updated successfully"))
                .addOnFailureListener(e -> Log.e("TAG", "Error updating points", e));
    }

    private void calculatePoints(){
        for(Place pl : placeList){
            if(pl.getNto100()==null){
                points+=2;
            }else{
                points+=5;
            }
        }
    }

    private void calculateLevel(){
        level = (points/100)+1;
    }

    private void displayCountOfPlaces(){
        TextView textViewPlacesCount = findViewById(R.id.textViewPlacesCount);
        textViewPlacesCount.setText("Брой посетени места: "+placeList.size());
    }

    private void fetchTopUsers() {
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.orderBy("points", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(task -> {
            if (!isFinishing()) { // Check if activity is still active
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        topUsers.add(user);
                    }
                    populateTopUsers();
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Method to populate top users in the second card view
    private void populateTopUsers() {
        topUsersLayout.removeAllViews(); // Clear existing views
        int i = 1;
        Log.d("POINTS", "count of users: "+topUsers.size());
        for (User user : topUsers) {
            Log.d("POINTS", "user: "+user.getEmail());
            // Create a TextView to display user information
            TextView textView = new TextView(this);
            if(i<=3){
                // Create a SpannableString to apply different styles
                SpannableString spannableString = new SpannableString(i + ") " + user.getEmail() + ": " + user.getPoints() + " точки");

                // Apply bold style to the substring containing the points
                int startIndex = i + user.getEmail().length() + 3; // Adjust the starting index
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView
                textView.setText(spannableString);
                textView.setTextSize(16);
                textView.setTypeface(null, Typeface.ITALIC);
                topUsersLayout.addView(textView);
            }
            if(email.equals(user.getEmail())){
                meTextView.setText(i+") "+user.getEmail() + ": " + user.getPoints()+" точки");
                meTextView.setTextSize(16);
            }
            i++;
        }
    }

}
