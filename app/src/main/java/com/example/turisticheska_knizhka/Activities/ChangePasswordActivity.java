package com.example.turisticheska_knizhka.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

public class ChangePasswordActivity extends AppCompatActivity {
    private String email;
    private BottomNavigationView bottomNavigationView;
    private CheckBox checkBoxShowPassword;
    EditText editTextOldPassword;
    EditText editTextNewPassword;
    EditText editTextConfirmPassword;
    Button buttonAcceptPassword;
    final LocalDatabase localDatabase = new LocalDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        email = getIntent().getStringExtra("email");

        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);
        buttonAcceptPassword = findViewById(R.id.buttonAcceptPassword);

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
        Navigation navigation = new Navigation(email, ChangePasswordActivity.this);
        navigation.bottomNavigation(bottomNavigationView);

        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Toggle password visibility based on checkbox state
            int inputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            editTextOldPassword.setInputType(inputType);
            editTextNewPassword.setInputType(inputType);
            editTextConfirmPassword.setInputType(inputType);
        });

        buttonAcceptPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                checkForExistingUser(email, PasswordHasher.hashPassword(oldPassword));
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAcceptButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editTextOldPassword.addTextChangedListener(textWatcher);
        editTextNewPassword.addTextChangedListener(textWatcher);
        editTextConfirmPassword.addTextChangedListener(textWatcher);
    }

    private void checkForExistingUser(String emailText, String hashPasswordText){
        QueryLocator.checkForExistingUser(emailText, hashPasswordText, new SingleUserCallback() {

            @Override
            public void onUserLoaded(User usr) {
                if (usr!=null) {
                    String newPassword = editTextNewPassword.getText().toString();
                    QueryLocator.updateUserSingleField(email, PasswordHasher.hashPassword(newPassword), "password");
                    localDatabase.updatePassword(email, PasswordHasher.hashPassword(newPassword));
                    Toast.makeText(ChangePasswordActivity.this, "Успешно променихте паролата си!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    // User with the given password does not exist
                    editTextOldPassword.setError("Невалидна стара парола!");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void updateAcceptButtonState() {
        boolean isEnabled = !editTextOldPassword.getText().toString().isEmpty() &&
                !editTextNewPassword.getText().toString().isEmpty() &&
                !editTextConfirmPassword.getText().toString().isEmpty() &&
                verifyNewPassword();


        // Set button state
        buttonAcceptPassword.setEnabled(isEnabled);

        // Set button color based on state
        //signUpButton.setBackgroundResource(isEnabled ? R.drawable.rounded_button_orange : R.drawable.rounded_button_yellow);
    }

    private boolean verifyNewPassword() {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        String passwordText = editTextNewPassword.getText().toString();
        String confirmPasswordText = editTextConfirmPassword.getText().toString();


        boolean isPasswordValid = passwordText.matches(passwordPattern);
        boolean arePasswordsMatching = passwordText.equals(confirmPasswordText);

        // Set error messages for each field
        editTextNewPassword.setError(isPasswordValid ? null : "Паролата трябва да съдържа поне 8 символа и да включва главни, малки букви и цифри!");
        editTextConfirmPassword.setError(arePasswordsMatching ? null : "Паролата не съвпада!");

        // Return true if all fields are valid
        return isPasswordValid && arePasswordsMatching;
    }
}