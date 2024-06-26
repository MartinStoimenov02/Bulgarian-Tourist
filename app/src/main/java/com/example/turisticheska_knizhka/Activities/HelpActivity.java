package com.example.turisticheska_knizhka.Activities;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class HelpActivity extends AppCompatActivity {

    private TextView textView;
    private Button btnNext;
    private Button btnBack;
    private String[] textContents;
    private int currentIndex = 0;
    private String email;
    ImageView imageView1;

    private FirebaseFirestore firestore;
    private int[] imageResources = {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4,
            R.drawable.slide5, R.drawable.slide6, R.drawable.slide7, R.drawable.slide8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help2);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        textView = findViewById(R.id.textView);
        imageView1 = findViewById(R.id.imageView1);

        btnBack.setVisibility(View.GONE);
        // Initialize text content
        textContents = getResources().getStringArray(R.array.help_text_contents);

        // Display initial text content
        updateContent(currentIndex);
    }

    /**
     * description Restrict return to previous activity with back arrow
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    // Method to update text content based on current index
    private void updateContent(int index) {
        if (index >= 0 && index < textContents.length) {
            textView.setText(textContents[index]);
            imageView1.setImageResource(imageResources[index]);
        }
    }

    // Method to handle next button click
    public void onNextButtonClick(View view) {
        if (currentIndex < textContents.length - 1) {
            currentIndex++;
            updateContent(currentIndex);
            // If moved forward to a text other than the first, show the back button
            if (currentIndex > 0) {
                btnBack.setVisibility(View.VISIBLE);
            }
            if (currentIndex == textContents.length - 1) {
                btnNext.setText("Завърши");
            }
        } else if (currentIndex == textContents.length - 1) {
            ProgressDialog progressDialog = ProgressDialog.show(HelpActivity.this, "Моля изчакайте", "Стартиране...", true, false);
            QueryLocator.removeIsFirstLoginStatus(email);
            // If currentIndex exceeds the length of textContents, open HomeActivity
            Intent intent = new Intent(HelpActivity.this, HomeActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
            // Implement your finish action here
        }
    }

    // Method to handle back button click
    public void onBackButtonClick(View view) {
        if (currentIndex > 0) {
            currentIndex--;
            updateContent(currentIndex);
            // If moved back to the first text, hide the back button again
            if (currentIndex == 0) {
                btnBack.setVisibility(View.GONE);
            }
            // If moved back to the text before the last, change next button text to "Следващ"
            if (currentIndex == textContents.length - 2) {
                btnNext.setText("Следващ");
            }
        }
    }
}

