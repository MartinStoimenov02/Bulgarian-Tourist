package com.example.turisticheska_knizhka;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;

public class Helper {
    public static boolean checkIsNTO(Place place){
        return place.getNto100() != null;
    }

    public static Place createPlaceFromNTO(String name, String urlMap, String workingHours, String placePhoneNumber, String imgPath, int distance, String userEmail, String ntoId){
        Log.d("ADD", "nto id: "+ntoId);
        Log.d("ADD", "nto id: "+userEmail);
        DocumentReference userRef = QueryLocator.getUserRef(userEmail);
        DocumentReference ntoRef = QueryLocator.getNTORef(ntoId);
        return new Place(name, urlMap, workingHours, placePhoneNumber, imgPath, distance, userRef, ntoRef);
    }

    public static void showOnMap(Context context, Object place){
        String urlMap = null;
        if (place instanceof Place) {
            Place placeObj = (Place) place;
            urlMap = placeObj.getUrlMap();
        } else if (place instanceof NTO100) {
            NTO100 nto100Obj = (NTO100) place;
            urlMap = nto100Obj.getUrlMap();
        }
        Log.d("URL", "url:"+urlMap);
        // Create an Intent with ACTION_VIEW to open the URL in a web browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlMap));
        context.startActivity(intent);
        // Verify that the intent will resolve to an activity
//        if (intent.resolveActivity(context.getPackageManager()) != null) {
//            // Start the activity if there is an app available to handle the intent
//            context.startActivity(intent);
//        } else {
//            // If no app is available to handle the intent, display a toast or handle it accordingly
//            Toast.makeText(context, "No app available to handle this action", Toast.LENGTH_SHORT).show();
//        }
    }
}
