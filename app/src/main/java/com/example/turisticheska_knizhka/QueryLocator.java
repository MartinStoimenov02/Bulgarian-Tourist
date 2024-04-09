package com.example.turisticheska_knizhka;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QueryLocator {

    public static void getMyPlaces(String email, PlacesCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = getUserRef(email);

        // Query the 'places' collection to get places associated with the provided user reference
        firestore.collection("places")
                .whereEqualTo("userEmail", userRef)
                .whereEqualTo("isVisited", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Place> places = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Place place = document.toObject(Place.class);
                        place.setId(document.getId());
                        //place.setFavourite(document.getBoolean("isFavourite"));
                        places.add(place);
                    }
                    callback.onPlacesLoaded(places); // Return the list of filtered places
                })
                .addOnFailureListener(callback::onError); // Return any database errors
    }

    public static void updateFavouriteStatus(String placeId, boolean isFavourite) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Log.d("Firestore", "place: " + placeId);
        DocumentReference placeRef = getPlaceRef(placeId);

        Log.d("Firestore", "placeRef: " + placeRef);

        Map<String, Object> updates = new HashMap<>();
        updates.put("isFavourite", isFavourite);

        Task<Void> updateTask = placeRef.update(updates);

        // Block until the update task completes
        while (!updateTask.isComplete()) {
            // Wait until the task is complete
        }

        if (updateTask.isSuccessful()) {
            // Update successful
        } else {
            // Update failed
        }
    }

    public static void getNto100(NTO100Callback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Query the 'places' collection to get places associated with the provided user reference
        firestore.collection("nto100")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<NTO100> nto100s = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        NTO100 nto100 = document.toObject(NTO100.class);
                        nto100.setId(document.getId());
                        nto100s.add(nto100);
                    }
                    callback.onNTO100Loaded(nto100s); // Return the list of filtered places
                })
                .addOnFailureListener(callback::onError); // Return any database errors
    }

    public static DocumentReference getUserRef(String email){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("users").document(email);
    }

    public static DocumentReference getPlaceRef(String placeId){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("places").document(placeId);
    }

    public static DocumentReference getNTORef(String ntoId){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("nto100").document(ntoId);
    }

    public static void getVisitedPlaces(String email, PlacesCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = getUserRef(email);

        firestore.collection("places")
                .whereEqualTo("userEmail", userRef)
                .whereEqualTo("isVisited", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Place> places = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Place place = document.toObject(Place.class);
                        place.setId(document.getId());
                        places.add(place);
                    }
                    callback.onPlacesLoaded(places); // Return the list of filtered places
                })
                .addOnFailureListener(callback::onError); // Return any database errors
    }

    public static void getNTO100ById(String placeId, SingleNTO100Callback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("nto100").document(placeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Convert the document snapshot to an NTO100 object
                    NTO100 nto100 = document.toObject(NTO100.class);
                    nto100.setId(document.getId());
                    callback.onNTOLoaded(nto100);
                } else {
                    // Handle document not found
                    callback.onError(new Exception("NTO100 not found"));
                }
            } else {
                // Handle task failure
                callback.onError(task.getException());
            }
        });
    }

    public static void getMyPlaceById(String placeId, SinglePlaceCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("places").document(placeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Convert the document snapshot to a Place object
                    Place place = document.toObject(Place.class);
                    callback.onPlaceLoaded(place); // Return the Place object via callback
                } else {
                    // Handle document not found
                    callback.onError(new Exception("Place not found"));
                }
            } else {
                // Handle task failure
                callback.onError(task.getException());
            }
        });
    }

    public static void addANewPlace(Place place, String ntoId){
        // Access the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("IDLOG", "id: "+place.getNto100());

        // Get a reference to the "places" collection
        DocumentReference placeRef = db.collection("places").document(ntoId);

        // Add the new place to the "places" collection
        placeRef.set(place)
                .addOnSuccessListener(documentReference -> {
                    // The place was added successfully
                    Log.d("TAG", "Place added with ID: " + documentReference);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("TAG", "Error adding place", e);
                });
    }


}

