package com.example.turisticheska_knizhka.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.turisticheska_knizhka.Callbacks.SingleUserCallback;
import com.example.turisticheska_knizhka.DataBase.LocalDatabase;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Helpers.PasswordHasher;
import com.example.turisticheska_knizhka.Models.User;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeleteProfileActivity extends AppCompatActivity {
    TextView textViewWarning;
    EditText editTextPassword;
    CheckBox checkBoxAccept;
    Button buttonDeleteProfile;
    private BottomNavigationView bottomNavigationView;
    final LocalDatabase localDatabase = new LocalDatabase(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        String email = getIntent().getStringExtra("email");

        textViewWarning = findViewById(R.id.textViewWarning);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxAccept = findViewById(R.id.checkBoxAccept);
        buttonDeleteProfile = findViewById(R.id.buttonDeleteProfile);

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
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        Navigation navigation = new Navigation(email, DeleteProfileActivity.this);
        navigation.bottomNavigation(bottomNavigationView);

        buttonDeleteProfile.setOnClickListener(view -> {
            // Check if the user has accepted the warning
            if (!checkBoxAccept.isChecked()) {
                checkBoxAccept.setError("Моля отбележете, че сте разбрали предупреждението!");
            }else{
                String password = editTextPassword.getText().toString().trim();
                checkForExistingUser(email, PasswordHasher.hashPassword(password));
            }
        });
    }

    private void checkForExistingUser(String emailText, String hashPasswordText){
        QueryLocator.checkForExistingUser(emailText, hashPasswordText, new SingleUserCallback() {

            @Override
            public void onUserLoaded(User usr) {
                if (usr!=null) {
                    ProgressDialog progressDialog = ProgressDialog.show(DeleteProfileActivity.this, "Моля изчакайте", "Изтриване на профил...", true, false);
                    QueryLocator.deleteAllLinkedPlaces(emailText);
                    localDatabase.deleteEmail(emailText);
                    QueryLocator.deleteUser(emailText);
                    Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User with the given password does not exist
                    editTextPassword.setError("Невалидна парола!");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}