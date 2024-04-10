package com.example.turisticheska_knizhka.Activities;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;

        import com.example.turisticheska_knizhka.DataBase.QueryLocator;
        import com.example.turisticheska_knizhka.Models.User;
        import com.example.turisticheska_knizhka.R;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.util.Random;

public class CodeVerificationActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String phone;
    private String hashedPassword;
    private String generatedCode;
    private EditText codeInput;
    String sEmail, sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set navigation back to SignUpView activity when toolbar navigation icon is clicked
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Get the email from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            phone = intent.getStringExtra("phone");
            hashedPassword = intent.getStringExtra("hashedPassword");
        }

        // Generate a random 6-digit code
        generatedCode = generateRandomCode();
        // Send the code to the provided email address (not implemented in this example)
        //SEND EMAIL!!!
        sendEmail(email, "Код за достъп до туристическата книжка", generateHtmlBody(name, generatedCode));
        // Find views
        codeInput = findViewById(R.id.codeInput);
        Button verifyButton = findViewById(R.id.verifyButton);
        Button resendButton = findViewById(R.id.resendButton);

        // Verify button click listener
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Compare the entered code with the generated code
                String enteredCode = codeInput.getText().toString().trim();
                if (enteredCode.equals(generatedCode)) {
                    ProgressDialog progressDialog = ProgressDialog.show(CodeVerificationActivity.this, "Моля изчакайте", "Извършва се регистрация...", true, false);
                    // Code is valid
                    QueryLocator.registerUser(name, email, phone, hashedPassword);
                    Intent intent = new Intent(CodeVerificationActivity.this, HelpActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    // Code is invalid
                    codeInput.setError("Невалиден код!");
                }
            }
        });

        // Resend button click listener
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regenerate a new random 6-digit code
                generatedCode = generateRandomCode();
                // Resend the code to the provided email address (not implemented in this example)
                //SEND EMAIL!!!
                sendEmail(email, "Код за достъп до туристическата книжка", generateHtmlBody(name, generatedCode));
            }
        });
    }

    // Method to generate a random 6-digit code
    private String generateRandomCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // Generates a random integer between 100000 and 999999
        return String.valueOf(code);
    }



    private void sendEmail(final String recipientEmail, final String subject, final String body) {
        Toast.makeText(CodeVerificationActivity.this, generatedCode, Toast.LENGTH_SHORT).show();
//        sEmail = "pmuproject24@gmail.com";
//        sPassword="2024-PMUproject";
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth","true");
//        properties.put("mail.smtp.starttls.enable","true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "587");
//
//        Session session1 = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(sEmail, sPassword);
//            }
//        });
//
//        try{
//            Message message = new MimeMessage(session1);
//            message.setFrom(new InternetAddress(sEmail));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("martinstoimenov02@gmail.com"));
//            message.setSubject(subject.trim());
//            message.setText(body.trim());
//            new SendMail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
//        }catch (MessagingException e){
//            e.printStackTrace();
//        }
//    }
//    private class SendMail extends AsyncTask<Message, String, String> {
//        private ProgressDialog progressDialog;
//        protected  void onPreExecute(){
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(CodeVerificationActivity.this, "Please wait", "Sending email...", true, false);
//
//        }
//        @Override
//        protected String doInBackground(Message... messages) {
//            try {
//                Transport.send(messages[0]);
//                return "Success";
//            } catch(MessagingException e) {
//                Log.e("AsyncTask", "Error in doInBackground: " + e.getMessage());
//                e.printStackTrace();
//                return "Error: " + e.getMessage(); // Return the error message
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            progressDialog.dismiss();
//            if (s.startsWith("Success")) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(CodeVerificationActivity.this);
//                builder.setCancelable(false);
//                builder.setTitle(Html.fromHtml("<font color='#509324'>Success</font>"));
//                builder.setMessage("Mail sent successfully.");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//            } else {
//                // Display the error message to the user
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//            }
//        }

    }




    private String generateHtmlBody(String name, String generatedCode){
        return
                "<html><head></head><body><h1>Здравейте "+name+"!</h1><br>"+
                        "<p>Добре дошли в нашата туристическа книжка!</p><br>"+
                        "<p>Вашият код за верификация е:</p><br>"+
                        "<h2>"+generatedCode+"</h2><br>"+
                        "<h3>Благодарим за доверието!</h3>"+
                        "</body></html>";
    }

}
