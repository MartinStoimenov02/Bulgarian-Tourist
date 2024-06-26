package com.example.turisticheska_knizhka.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.turisticheska_knizhka.Callbacks.SingleUserCallback;
import com.example.turisticheska_knizhka.DataBase.LocalDatabase;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.PasswordHasher;
import com.example.turisticheska_knizhka.Models.User;
import com.example.turisticheska_knizhka.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private ConstraintLayout loginLayout;
    private EditText password;
    private EditText email;
    private Button loginButton;
    private Button signupButton;
    private CheckBox showPasswordCheckBox;
    private CheckBox rememberMeCheckbox;
    private FirebaseFirestore firestore;
    final LocalDatabase localDatabase = new LocalDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView3);
        loginLayout = findViewById(R.id.loginLayout);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.signup);
        showPasswordCheckBox = findViewById(R.id.showPassword);
        rememberMeCheckbox = findViewById(R.id.rememberMe);
        setImageRandomly();
        setupKeyboardListener();
        // Add TextChangedListeners to EditText fields
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        // Disable buttons initially and set colors

        List<String> rememberedEmails = localDatabase.getEmailsWithRememberMe();
        Log.d("REMEMBER", rememberedEmails.toString());
        if(rememberedEmails.size()==1){
            navigateToHomeActivity(rememberedEmails.get(0));
        }
        else {
            List<String> allEmails = localDatabase.getAllEmails();
            if(allEmails.size()>0){
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Моля потвърдете!")
                        .setDescription("задължителна ауторизация!")
                        .setNegativeButtonText("откажи")
                        .build();
                getPrompt().authenticate(promptInfo);
            }

            updateButtonState();
            showPassword();
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create intent to start PlaceView activity
                    Intent intent = new Intent(MainActivity.this, SignUpView.class);
                    // Start the activity
                    startActivity(intent);
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkForExistingUser(email.getText().toString(), PasswordHasher.hashPassword(password.getText().toString()), rememberMeCheckbox.isChecked());
                }
            });
        }
    }

    private BiometricPrompt getPrompt(){
        Executor executor= ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                List<String> allEmails = localDatabase.getAllEmails();
                if(allEmails.size()>1){chooseEmailToLogin(allEmails);}
                else if(allEmails.size()==1){checkForExistingUser(allEmails.get(0), localDatabase.getHashedPassword(allEmails.get(0)), false);}
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("достъп отказан!");
            }
        };
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
        return biometricPrompt;
    }

    private void chooseEmailToLogin(List<String> allEmails) {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_emails, null);

        // Initialize the ListView
        ListView userListView = popupView.findViewById(R.id.userListView);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, allEmails){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, allEmails);
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // Set text color to white
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.white));
                return view;
            }
        };
        userListView.setAdapter(adapter);

        // Set item click listener for the ListView
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected user's email
                String selectedEmail = (String) parent.getItemAtPosition(position);

                // Fetch additional information from the local database based on the selected email
                String hashedPassword = localDatabase.getHashedPassword(selectedEmail);

                // Call a method to check for the existing user using the selected email and hashed password
                checkForExistingUser(selectedEmail, hashedPassword, false);
            }
        });

        // Set item long click listener for the ListView to delete user
        userListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected user
                String selectedEmail = (String) parent.getItemAtPosition(position);

                // Show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Премахване на имейл?");
                builder.setMessage("Сигурни ли сте, че искате да премахнете имейла?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the selected user from the list
                        allEmails.remove(position);
                        // Update the ListView
                        adapter.notifyDataSetChanged();
                        localDatabase.deleteEmail(selectedEmail);
                    }
                });
                builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, just dismiss the dialog
                    }
                });
                builder.show();

                return true; // Consume the long click event
            }
        });

        // Create the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void notifyUser(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkForExistingUser(String emailText, String hashPasswordText, boolean rememberMe){
        QueryLocator.checkForExistingUser(emailText, hashPasswordText, new SingleUserCallback() {

            @Override
            public void onUserLoaded(User usr) {
                if (usr!=null) {
                    //ако имейла не съществува локлано...
                    if (!savedEmailExists(emailText)) {
                        //... но е избрал функция "Запомни ме"...
                        if (rememberMe) {
                            //...той директно се добавя в локалната база данни с включена функция "Запомни ме"
                            localDatabase.addEmail(emailText, hashPasswordText, true);
                        }else{
                            //...в противен случай му се предлага да активира функцията за пръстов отпечатък
                            activateFingerPrintForEmail(emailText, hashPasswordText);
                        }
                    }
                    //ако имейла вече съществува локално, след верификацията се пренасочва към началната страница/помощното меню
                    else {
                        if(rememberMe){
                            localDatabase.updatePassword(emailText, hashPasswordText, true);
                        }
                        checkIsFirstLogin(emailText);
                    }
                } else {
                    // User with the given email and password does not exist
                    // Show error or handle as per your requirement
                    if(savedEmailExists(emailText)){localDatabase.deleteEmail(emailText);}
                    notifyUser("Потребител с този имейл вече не съществува! Изберете друг!");
                    password.setError("потребител с такъв имейл или парола не същестува!");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void activateFingerPrintForEmail(String email, String password) {
        // Create a popup window with a message prompting the user to add the email to the local database
        // and two buttons (Yes and No)
        PopupWindow popupWindow = new PopupWindow(this);
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        popupWindow.setContentView(popupView);

        // Set text of the popup message
        TextView popupText = popupView.findViewById(R.id.popupText);
        popupText.setText("Искате ли да активирате вход с пръстов отпечатък за този имейл адрес?");

        // Handle button clicks
        Button btnYes = popupView.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add email to the local database
                localDatabase.addEmail(email, password, false);
                // Dismiss the popup window
                popupWindow.dismiss();
                checkIsFirstLogin(email);
            }
        });

        Button btnNo = popupView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                popupWindow.dismiss();
                checkIsFirstLogin(email);
            }
        });

        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private boolean savedEmailExists(String email){
        List<String> savedEmails = localDatabase.getAllEmails();
        boolean flag = false;
        for(String emailObj : savedEmails){
            if(emailObj.equals(email)){
                flag=true;
                break;
            }
        }
        return flag;
    }

    private void navigateToHomeActivity(String emailText){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("email", emailText);
        startActivity(intent);
        finish();
    }

    private void showPassword(){
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Toggle password visibility based on checkbox state
            if (isChecked) {
                // Show password
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Hide password
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move cursor to the end of the text
            password.setSelection(password.getText().length());
        });
    }

    private void redirectToHelp(String emailText){
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        intent.putExtra("email", emailText);
        startActivity(intent);
        finish();
    }

    private void checkIsFirstLogin(String emailText) {
        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Моля изчакайте", "Влизане...", true, false);
        // Get Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Query for the user document with the provided email
        firestore.collection("users")
                .whereEqualTo("email", emailText)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming there is only one document per user, get the first document
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        // Retrieve the value of isFirstLogin field from the document
                        Boolean isFirstLogin = documentSnapshot.getBoolean("loginFirst");

                        if (isFirstLogin != null && isFirstLogin) {
                            // If isFirstLogin is true, redirect to help activity
                            redirectToHelp(emailText);
                        }else{
                            navigateToHomeActivity(emailText);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that may occur
                    e.printStackTrace();
                });
    }

    // TextWatcher to listen for changes in EditText fields
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No action needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Check if EditText fields are empty and update button state
            updateButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed
        }
    };

    // Method to update button state and colors
    private void updateButtonState() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        // Enable/disable buttons based on EditText fields' content
        loginButton.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());

        loginButton.setTextColor(loginButton.isEnabled() ? getResources().getColor(R.color.white) : getResources().getColor(R.color.green));
        loginButton.setBackgroundResource(loginButton.isEnabled() ? R.drawable.rounded_button_green : R.drawable.rounded_button_white_green);
    }

    private void setImageRandomly() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the URL for the search request
                    URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" +
                            "AIzaSyBSrFIVWfPGscGFskb3s3tl1crSYL5lq9A" + "&cx=" + "8662ccaade9c34971" + "&searchType=image&q=" + "обекти в българия");

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Read the response
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder responseBuilder = new StringBuilder();
                    int data;
                    while ((data = inputStream.read()) != -1) {
                        responseBuilder.append((char) data);
                    }
                    inputStream.close();

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                    JSONArray items = jsonResponse.getJSONArray("items");

                    // Get a random image URL from the search results
                    Random random = new Random();
                    int randomIndex = random.nextInt(items.length());
                    String imageUrl = items.getJSONObject(randomIndex).getString("link");

                    // Load the image using Glide on the main UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!isDestroyed()) {
                                Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.placeholder_image)
                                        .error(R.drawable.error_image)
                                        .into(imageView);
                            }

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setupKeyboardListener() {
        final View mainLayout = findViewById(R.id.loginLayout); // Change to your main layout id
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mainLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mainLayout.getRootView().getHeight();

                // Calculate the height difference between the root view and the visible display frame
                int heightDifference = screenHeight - (r.bottom - r.top);

                // If the height difference is greater than 200 pixels, it's probably a keyboard...
                if (heightDifference > 200) {
                    // Move the views up by the height difference
                    moveViewsUp(heightDifference);
                } else {
                    // Reset the views to their original positions
                    resetViewsPosition();
                }
            }
        });
    }

    private void moveViewsUp(int heightDifference) {
        // Adjust the position of the views here
        // For example, you can use translationY property to move views up
        loginLayout.setTranslationY(heightDifference/3); // Adjust according to your layout
        imageView.setVisibility(View.GONE);
    }

    private void resetViewsPosition() {
        // Reset the position of the views to their original positions
        loginLayout.setTranslationY(0);
        imageView.setVisibility(View.VISIBLE);
    }
}
