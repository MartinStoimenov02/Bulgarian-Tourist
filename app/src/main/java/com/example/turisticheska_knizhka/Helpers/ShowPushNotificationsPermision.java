package com.example.turisticheska_knizhka.Helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.turisticheska_knizhka.Callbacks.SingleUserCallback;
import com.example.turisticheska_knizhka.DataBase.QueryLocator;
import com.example.turisticheska_knizhka.Models.User;

import java.util.Objects;

public class ShowPushNotificationsPermision {
    private static boolean isServiceRunning = false;

    public static void showNotifications(String email, Context context) {
        QueryLocator.getUserByEmail(email, new SingleUserCallback() {
            @Override
            public void onUserLoaded(User usr) {
                if (usr != null) {
                    if (usr.getNotifications() && !isServiceRunning) {
                        startService(context, email);
                    } else if (!usr.getNotifications() && isServiceRunning) {
                        stopService(context);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("GetUsers", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    private static void startService(Context context, String email) {
        Intent intent = new Intent(context, DistanceCalculationService.class);
        intent.putExtra("email", email);
        context.startService(intent);
        isServiceRunning = true;
    }

    private static void stopService(Context context) {
        Intent intent = new Intent(context, DistanceCalculationService.class);
        context.stopService(intent);
        isServiceRunning = false;
    }
}

