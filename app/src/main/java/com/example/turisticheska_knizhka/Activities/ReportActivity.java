package com.example.turisticheska_knizhka.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Helpers.Navigation;
import com.example.turisticheska_knizhka.Models.Report;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;

public class ReportActivity extends AppCompatActivity {
    private String email;
    private BottomNavigationView bottomNavigationView;
    Button reportButton;
    EditText reportTitle;
    EditText reportText;
    Spinner gradeComboBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        email = getIntent().getStringExtra("email");
        reportButton = findViewById(R.id.reportButton);
        reportTitle = findViewById(R.id.reportTitle);
        gradeComboBox = findViewById(R.id.gradeComboBox);
        reportText = findViewById(R.id.reportText);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Navigation navigation = new Navigation(email, ReportActivity.this);
        navigation.bottomNavigation(bottomNavigationView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate custom layout for dropdown item
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.WHITE); // Set white background color
                return textView;
            }
        };

        for (int i = 0; i <= 5; i++) {
            adapter.add(String.valueOf(i));
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeComboBox.setAdapter(adapter);
        gradeComboBox.setSelection(0);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReport();
            }
        });
    }

    private void addReport(){
        String title = reportTitle.getText().toString();
        int grade = Integer.parseInt(gradeComboBox.getSelectedItem().toString());
        String reportBody = reportText.getText().toString();

        if(grade==0){
            Toast.makeText(this, "Оценката трябва да е по-голяма от 0", Toast.LENGTH_LONG).show();
            return;
        }
        if (title.equals("")){
            reportTitle.setError("Полето трябва да е попълнено!");
            return;
        }
        if (reportBody.equals("")){
            reportText.setError("Полето трябва да е попълнено!");
            return;
        }

        DocumentReference userRef = QueryLocator.getUserRef(email);
        Report userReport = new Report(title, reportBody, grade, userRef);
        QueryLocator.saveReport(userReport);
        Navigation nav = new Navigation(email, this);
        nav.navigateToHomeActivity();
    }
}