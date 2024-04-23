package com.example.turisticheska_knizhka.Helpers;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.turisticheska_knizhka.Callbacks.PlacesCallback;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Models.Place;

import java.util.List;

public class DistanceCalculationService extends IntentService {
    private Handler handler = new Handler();
    private boolean isNotificationAllowed = true;

    public DistanceCalculationService() {
        super("DistanceCalculationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String email = intent.getStringExtra("email");
            listNearestPlaces(email);
        }
    }

    private void listNearestPlaces(String email) {
        QueryLocator.getMyUnvisitedPlaces(email, new PlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                for (Place pl : places) {
                    Helper.getCurrentLocation(DistanceCalculationService.this, new com.example.turisticheska_knizhka.Callbacks.LocationCallback() {
                        @Override
                        public void onLocationResult(Location location) {
                            if (location != null) {
                                int distance = (int) Helper.calculateDistance(pl.getUrlMap(), location);
                                Log.d("DISTANCE", "Distance: " + distance);
                                if (distance < 10000  && isNotificationAllowed) {
                                    //push notifications
                                    sendNotification("Близка дестинация!", pl.getName()+" е в близост до Вас!");
                                    isNotificationAllowed = false;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isNotificationAllowed = true;
                                        }
                                    }, 60000); // 10000 milliseconds = 10 seconds
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

    // Метод за създаване и изпращане на уведомление
    private void sendNotification(String title, String message) {
        Log.d("SENDNOT", "sendNotification: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myCh", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myCh")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle(title)
                .setContentText(message);

        Notification notification = builder.build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(1, notification);
    }

    public Context getContext() {
        return this;
    }
}