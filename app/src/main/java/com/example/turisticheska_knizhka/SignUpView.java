package com.example.turisticheska_knizhka;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.turisticheska_knizhka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class SignUpView extends AppCompatActivity {

    private EditText name, email, phone, password, confirmPassword;
    private Button signUpButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_view);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Find views
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUpButton = findViewById(R.id.signupButton);

        // Set navigation on toolbar click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to previous activity
            }
        });

        // Text change listener for enabling/disabling signup button
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSignUpButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Add text change listener to all EditText fields
        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);

        // Initial state of signup button
        updateSignUpButtonState();

        // Show password checkbox
        CheckBox showPasswordCheckBox = findViewById(R.id.showPassword);
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        // Sign up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForExistingEmail();
            }
        });
    }

    private void checkForExistingEmail() {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("email", email.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() != 0) {
                                // Email already exists, show error and return
                                email.setError("Потребител с такъв имейл вече съществува!");
                            } else {
                                // Email does not exist, proceed to sign up
                                checkForExistingPhoneNumber();
                            }
                        } else {
                            // Error occurred while fetching data
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void checkForExistingPhoneNumber() {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("phone", phone.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() != 0) {
                                // Email already exists, show error and return
                                phone.setError("Потребител с такъв телефонен номер вече съществува!");
                            } else {
                                // Email does not exist, proceed to sign up
                                //ProgressDialog progressDialog = ProgressDialog.show(SignUpView.this, "Моля изчакайте", "Изпращане на имейл...", true, false);
                                navigateToCodeVerificationActivity();
                            }
                        } else {
                            // Error occurred while fetching data
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // Method to navigate to code verification activity
    private void navigateToCodeVerificationActivity() {
        Intent intent = new Intent(SignUpView.this, CodeVerificationActivity.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("phone", phone.getText().toString());
        intent.putExtra("hashedPassword", PasswordHasher.hashPassword(password.getText().toString()));
        startActivity(intent);
    }

    // Method to update state of signup button
    @SuppressLint("ResourceAsColor")
    private void updateSignUpButtonState() {
        boolean isEnabled = !name.getText().toString().isEmpty() &&
                !email.getText().toString().isEmpty() &&
                !phone.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !confirmPassword.getText().toString().isEmpty() && verifyFields();


        // Set button state
        signUpButton.setEnabled(isEnabled);

        // Set button color based on state
        //signUpButton.setBackgroundResource(isEnabled ? R.drawable.rounded_button_orange : R.drawable.rounded_button_yellow);
    }

    // Method to verify fields before signup and set error messages
    private boolean verifyFields() {
        String emailPattern = "([0-9]|[A-Z]|[a-z]|_|-)+@([a-z]|-|_)+.[a-z]+";
        String phonePattern = "(\\+|[0-9]+\\/)?[0-9]{6,}"; //   02/9771834, +359885567787, 055/8441231
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        String nameText = name.getText().toString();
        String emailText = email.getText().toString();
        String phoneText = phone.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();

        boolean isNameValid = !nameText.isEmpty();
        boolean isEmailValid = emailText.matches(emailPattern);
        boolean isPhoneValid = phoneText.matches(phonePattern);
        boolean isPasswordValid = passwordText.matches(passwordPattern);
        boolean arePasswordsMatching = passwordText.equals(confirmPasswordText);

        // Set error messages for each field
        name.setError(isNameValid ? null : "Моля въведете Вашето име!");
        email.setError(isEmailValid ? null : "Моля въведете валиден имейл адрес!");
        phone.setError(isPhoneValid ? null : "Моля въведете валиден телефонен номер!");
        password.setError(isPasswordValid ? null : "Паролата трябва да съдържа поне 8 символа и да включва главни, малки букви и цифри!");
        confirmPassword.setError(arePasswordsMatching ? null : "Паролата не съвпада!");

        // Return true if all fields are valid
        return isNameValid && isEmailValid && isPhoneValid && isPasswordValid && arePasswordsMatching;
    }
}
