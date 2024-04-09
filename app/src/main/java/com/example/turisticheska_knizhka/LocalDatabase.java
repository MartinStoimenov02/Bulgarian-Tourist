package com.example.turisticheska_knizhka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocalDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "localDB";
    private static final String TABLE_EMAILS = "saved_emails";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "email";
    private static final String KEY_PH_NO = "Password";

    public LocalDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Създаване на Таблицата
    @Override public void onCreate(SQLiteDatabase db) {
        String CREATE_EMAIL_TABLE = "CREATE TABLE " + TABLE_EMAILS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_EMAIL_TABLE);
    }

    // Добавяне на нов Потребител
    void addEmail(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, email);
        values.put(KEY_PH_NO, password);
        db.insert(TABLE_EMAILS, null, values);
        db.close();
    }

    // Вземане на всички Потребители
    public List<String> getAllEmails() {
        List<String> emailList = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_EMAILS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Обхождане на всички редове и избор
        if (cursor.moveToFirst()) {
            do {
                emailList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return emailList;
    }

    // Изтриване на Потребител
    public void deleteEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMAILS, KEY_NAME + " = ?", new String[] {
                String.valueOf(email)
        });
        db.close();
    }

    // Method to retrieve hashed password based on email
    public String getHashedPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = null;

        // Define the query to fetch the hashed password based on the email
        String query = "SELECT password FROM saved_emails WHERE email = ?";

        // Execute the query with the provided email as a selection argument
        Cursor cursor = db.rawQuery(query, new String[]{email});
        //Check if the cursor has any results
        if (cursor.moveToFirst()) {
            // Retrieve the column index of the "password" column
            int passwordColumnIndex = cursor.getColumnIndex(KEY_PH_NO);

            // Check if the column index is valid (not -1)
            if (passwordColumnIndex != -1) {
                // Retrieve the hashed password from the cursor
                hashedPassword = cursor.getString(passwordColumnIndex);
            } else {
                // Handle the case where the "password" column is not found
                // For example, you could log an error or throw an exception
                Log.e("LocalDatabase", "Column 'password' not found in cursor");
            }
        }

        //Close the cursor and database connection
        cursor.close();
        db.close();

        return hashedPassword;
    }

}
