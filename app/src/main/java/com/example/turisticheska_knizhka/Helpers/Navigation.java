package com.example.turisticheska_knizhka.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.turisticheska_knizhka.Activities.HelpActivity;
import com.example.turisticheska_knizhka.Activities.HomeActivity;
import com.example.turisticheska_knizhka.Activities.NearestActivity;
import com.example.turisticheska_knizhka.Activities.PlaceListView;
import com.example.turisticheska_knizhka.Activities.ProfileActivity;
import com.example.turisticheska_knizhka.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Navigation {
    private String email;
    private Context context;
    public Navigation(String email, Context context){
        this.email = email;
        this.context = context;
    }
    public void bottomNavigation(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId()== R.id.action_home){
                navigateToHomeActivity();
                return true;
            } else if(item.getItemId()==R.id.action_my_places){
                navigateToPlaceListView(1);
                return true;
            } else if(item.getItemId()==R.id.action_nto100){
                navigateToPlaceListView(2);
                return true;
            } else if(item.getItemId()==R.id.action_nearest){
                navigateToNearestActivity();
                return true;
            } else if(item.getItemId()==R.id.action_profile){
                navigateToProfileActivity();
                return true;
            }
            return false;
        });
    }

    public void topMenu(BottomNavigationView topView){
        topView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.action_help){
                navigateToHelp();
                return true;
            }
//            else if(item.getItemId()==R.id.action_notifications){
                //navigate to notifications
//                return true;
//            }
            return false;
        });
    }

    private void navigateToHelp(){
        Intent intent = new Intent(context, HelpActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    private void navigateToHomeActivity(){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    public void navigateToPlaceListView(int caseNumber){
        Intent intent = new Intent(context, PlaceListView.class);
        intent.putExtra("email", email);
        intent.putExtra("caseNumber", caseNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    private void navigateToProfileActivity(){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    private void navigateToNearestActivity(){
        Intent intent = new Intent(context, NearestActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
