package com.example.turisticheska_knizhka.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NearestActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_nearest);
        BottomNavigationView topMenu = findViewById(R.id.top_menu);
        topMenu.setSelectedItemId(R.id.space);
        Navigation navigation = new Navigation(email, NearestActivity.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.topMenu(topMenu);
    }
}