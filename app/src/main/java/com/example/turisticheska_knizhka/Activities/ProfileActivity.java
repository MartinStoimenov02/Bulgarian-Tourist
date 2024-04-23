package com.example.turisticheska_knizhka.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.turisticheska_knizhka.Callbacks.SingleUserCallback;
import com.example.turisticheska_knizhka.DataBase.LocalDatabase;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Helpers.ShowPushNotificationsPermision;
import com.example.turisticheska_knizhka.Models.User;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    String email;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView topMenu;
    private EditText editTextName;
    private ImageView buttonEditName;
    private ImageView buttonAcceptName;
    private EditText editTextPhone;
    private ImageView buttonEditPhone;
    private ImageView buttonAcceptPhone;
    private CheckBox notificationCheckbox;
    private Button buttonChangePassword;
    private Button buttonDeleteProfile;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }

        editTextName=findViewById(R.id.editTextName);
        buttonEditName = findViewById(R.id.buttonEditName);
        buttonAcceptName = findViewById(R.id.buttonAcceptName);
        editTextPhone=findViewById(R.id.editTextPhone);
        buttonEditPhone = findViewById(R.id.buttonEditPhone);
        buttonAcceptPhone = findViewById(R.id.buttonAcceptPhone);
        notificationCheckbox = findViewById(R.id.notificationCheckbox);
        editTextName.setEnabled(false);
        editTextPhone.setEnabled(false);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteProfile = findViewById(R.id.buttonDeleteProfile);
        buttonLogout = findViewById(R.id.buttonLogout);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        topMenu = findViewById(R.id.top_menu);
        topMenu.setSelectedItemId(R.id.space);

        // Set up bottom navigation view
        Navigation navigation = new Navigation(email, ProfileActivity.this);
        navigation.bottomNavigation(bottomNavigationView);
        navigation.topMenu(topMenu);

        getUserInfo();
        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setEnabled(true);
                buttonEditName.setVisibility(View.GONE);
                buttonAcceptName.setVisibility(View.VISIBLE);
            }
        });

        buttonEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPhone.setEnabled(true);
                buttonEditPhone.setVisibility(View.GONE);
                buttonAcceptPhone.setVisibility(View.VISIBLE);
            }
        });

        buttonAcceptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setEnabled(false);
                QueryLocator.updateUserSingleField(email, String.valueOf(editTextName.getText()), "name");
                buttonAcceptName.setVisibility(View.GONE);
                buttonEditName.setVisibility(View.VISIBLE);
            }
        });

        buttonAcceptPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPhone.setEnabled(false);
                QueryLocator.updateUserSingleField(email, String.valueOf(editTextPhone.getText()), "phone");
                buttonAcceptPhone.setVisibility(View.GONE);
                buttonEditPhone.setVisibility(View.VISIBLE);
            }
        });

        notificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            QueryLocator.updateUserSingleField(email, notificationCheckbox.isChecked(), "notifications");
            ShowPushNotificationsPermision.showNotifications(email, ProfileActivity.this);
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        buttonDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, DeleteProfileActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = ProgressDialog.show(ProfileActivity.this, "Моля изчакайте", "Излизане...", true, false);
                LocalDatabase sqlLite = new LocalDatabase(ProfileActivity.this);
                List<String> rememberedEmails = sqlLite.getEmailsWithRememberMe();
                if(rememberedEmails.size()!=0 && email.equals(rememberedEmails.get(0))){
                    sqlLite.deleteEmail(email);
                }
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUserInfo(){
        QueryLocator.getUserByEmail(email, new SingleUserCallback() {
            @Override
            public void onUserLoaded(User usr) {
                // Handle the loaded NTO100 object
                if (usr != null) {
                    editTextName.setText(usr.getName());
                    editTextPhone.setText(usr.getPhone());
                    Log.d("CHECKBOX", "not: "+usr.getNotifications());
                    notificationCheckbox.setChecked(usr.getNotifications());
                } else {
                    // Handle case where NTO100 is null
                    Toast.makeText(ProfileActivity.this, "NTO100 not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Toast.makeText(ProfileActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
