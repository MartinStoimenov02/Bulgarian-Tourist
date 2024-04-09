package com.example.turisticheska_knizhka;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    String email;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView topMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        topMenu = findViewById(R.id.top_menu);
        topMenu.setSelectedItemId(R.id.space);

        // Set up bottom navigation view
        Navigation navigation = new Navigation(email, ProfileActivity.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.topMenu(topMenu);
    }
}
