package com.example.turisticheska_knizhka;

import android.util.Log;
import android.view.View;

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
}
