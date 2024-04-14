package com.example.turisticheska_knizhka.DataBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.turisticheska_knizhka.Callbacks.NTO100Callback;
import com.example.turisticheska_knizhka.Callbacks.PlacesCallback;
import com.example.turisticheska_knizhka.Callbacks.SingleNTO100Callback;
import com.example.turisticheska_knizhka.Callbacks.SinglePlaceCallback;
import com.example.turisticheska_knizhka.Callbacks.SingleUserCallback;
import com.example.turisticheska_knizhka.Models.NTO100;
import com.example.turisticheska_knizhka.Models.Place;
import com.example.turisticheska_knizhka.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryLocator {
    public static void getUserByEmail(String email, SingleUserCallback callback){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Convert the document snapshot to an NTO100 object
                    User usr = document.toObject(User.class);
                    usr.setEmail(document.getId());
                    callback.onUserLoaded(usr);
                } else {
                    // Handle document not found
                    callback.onError(new Exception("user not found"));
                }
            } else {
                // Handle task failure
                callback.onError(task.getException());
            }
        });
    }

    public static void checkForExistingUser(String emailText, String hashPasswordText, SingleUserCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .whereEqualTo("email", emailText) // Filter by email
                .whereEqualTo("password", hashPasswordText) // Filter by password
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // If documents are found, assume only one user document is found
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            // Convert the document snapshot to a User object
                            User user = document.toObject(User.class);
                            user.setEmail(document.getId());
                            callback.onUserLoaded(user);
                        } else {
                            // No documents found with the given email and password
                            callback.onUserLoaded(null);
                        }
                    } else {
                        // Handle task failure
                        callback.onError(task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    // Log the failure
                    Log.e("USER", "Error retrieving user: ", e);
                    // Pass the error to the callback
                    callback.onError(e);
                });
    }

    public static void updateUserSingleField(String email, String value, String field){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(email).update(field, value)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});
        Log.d("UPDATE", "user "+email+" updated successfully! field: "+field+"; value: "+value);
    }

    public static void updateUserSingleField(String email, boolean value, String field){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(email).update(field, value)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});
        Log.d("UPDATE", "user "+email+" updated successfully! field: "+field+"; value: "+value);
    }

    public static void getAllMyPlaces(String email, PlacesCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = getUserRef(email);

        // Query the 'places' collection to get places associated with the provided user reference
        firestore.collection("places")
                .whereEqualTo("userEmail", userRef)
                //.orderBy("name")
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

    public static void getMyUnvisitedPlaces(String email, PlacesCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = getUserRef(email);

        // Query the 'places' collection to get places associated with the provided user reference
        firestore.collection("places")
                .whereEqualTo("userEmail", userRef)
                .whereEqualTo("isVisited", false)
                //.orderBy("name")
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
        firestore.collection("nto100").whereEqualTo("isActive", true)
                //.orderBy("numberInNationalList")
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
                //.orderBy("name")
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
                    place.setId(document.getId());
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


    public static void registerUser(String name, String email, String phone, String hashedPassword) {
        // Create a new User object with the user's information
        User user = new User(name, email, phone, hashedPassword);


        // Get a reference to the Firestore database
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Add the user to the "users" collection in Firestore
        firestore.collection("users").document(email).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User successfully added to Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add user to Firestore
                        Log.e("Firestore", "Error adding user", e);
                    }
                });
    }

    public static void removeIsFirstLoginStatus(String email){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Update the loginFirst field to false
                            firestore.collection("users")
                                    .document(document.getId())
                                    .update("loginFirst", false)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update successful
                                        // You can perform any additional actions here if needed
                                        Log.d("Firestore", "Login status updated successfully for user with email: " + email);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error handling
                                        Log.e("Firestore", "Error updating login status for user with email: " + email, e);
                                    });
                        }
                    } else {
                        // Error handling
                        Log.e("Firestore", "Error getting user document with email: " + email, task.getException());
                    }
                });
    }

    public static void addPlaceToMyPlaces(Place newPlace){
        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the "places" collection
        CollectionReference placesCollectionRef = db.collection("places");

        // Add the new place to the "places" collection
        placesCollectionRef.add(newPlace)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Place added successfully
                            Log.d("TAG", "Place added with ID: " + task.getResult().getId());
                        } else {
                            // Failed to add place
                            Log.w("TAG", "Error adding place", task.getException());
                        }
                    }
                });
    }

    public static void updatePlaceDistance(Object obj, int distance){
        if(obj instanceof Place){
            Place place = (Place) obj;
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("places").document(place.getId()).update("distance", distance)
                   .addOnSuccessListener(aVoid -> {})
                   .addOnFailureListener(e -> {});
        }else if (obj instanceof NTO100){
            NTO100 nto100 = (NTO100) obj;
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("nto100").document(nto100.getId()).update("distance", distance)
                    .addOnSuccessListener(aVoid -> {})
                    .addOnFailureListener(e -> {});
        }
    }

    public static void updatePlaceVisitation(Place place){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("places").document(place.getId()).update("isVisited", place.getIsVisited())
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});
    }

    public static void deleteUser(String email){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firestore.collection("users").document(email);

        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "DocumentSnapshot successfully deleted: " + email);
                    // Handle success (optional)
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error deleting document", e);
                    // Handle failure (optional)
                });
    }

    public static void deleteAllLinkedPlaces(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference userRef = getUserRef(email);

        // Query to retrieve all documents where the userEmail field matches the provided email
        Query query = firestore.collection("places").whereEqualTo("userEmail", userRef);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Iterate over the documents and delete each one
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String documentId = document.getId();
                        deletePlace(documentId);
                    }
                    Log.d("Firebase", "All linked places successfully deleted for user: " + email);
                    // Handle success (optional)
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error deleting linked places", e);
                    // Handle failure (optional)
                });
    }

    public static void deletePlace(String documentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("places").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "DocumentSnapshot successfully deleted: " + documentId);
                    // Handle success (optional)
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error deleting document", e);
                    // Handle failure (optional)
                });
    }
}

