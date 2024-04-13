package com.example.turisticheska_knizhka.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.turisticheska_knizhka.Activities.PlaceView;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Models.NTO100;
import com.example.turisticheska_knizhka.Models.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentReference;

public class Helper {
    public static boolean checkIsNTO(Place place){
        return place.getNto100() != null;
    }

    public static Place createPlaceFromNTO(String name, String urlMap, String imgPath, int distance, String userEmail, String ntoId, String description){
        Log.d("ADD", "nto id: "+ntoId);
        Log.d("ADD", "nto id: "+userEmail);
        DocumentReference userRef = QueryLocator.getUserRef(userEmail);
        DocumentReference ntoRef = QueryLocator.getNTORef(ntoId);
        return new Place(name, urlMap, imgPath, distance, userRef, ntoRef, description);
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

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static float calculateDistance(String urlMap, Location currentLocation) {
        // Extract latitude and longitude from the URL
        String[] parts = urlMap.split("[=,]");
        if(parts.length<2){
            return -1;
        }
        double latitude = Double.parseDouble(parts[1]);
        double longitude = Double.parseDouble(parts[2]);

        // Create destination location
        Location destinationLocation = new Location("Destination");
        destinationLocation.setLatitude(latitude);
        destinationLocation.setLongitude(longitude);

        // Calculate distance between current location and destination
        return currentLocation.distanceTo(destinationLocation);
    }

    public static void getCurrentLocation(PlaceView context, com.example.turisticheska_knizhka.Callbacks.LocationCallback locationCallback) {
        // Create a location request
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1) // Update location every 1 minute
                .setFastestInterval(1); // Fastest interval for location updates

        // Create a location callback
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                // Get the most recent location
                Location location = locationResult.getLastLocation();
                // Call the callback with the obtained location
                locationCallback.onLocationResult(location);
            }
        };

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the FusedLocationProviderClient instance
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } else {
            // Handle the case where location permissions are not granted
            Log.e("Location", "Location permission not granted");
            // You may want to show a message or request permissions here
        }
    }

}
