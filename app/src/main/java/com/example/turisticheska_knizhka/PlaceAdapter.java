package com.example.turisticheska_knizhka;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Place> {

    private Context context;
    private List<Place> places;
    private String email;
    private int caseNumber;

    public PlaceAdapter(Context context, List<Place> places, String email, int caseNumber) {
        super(context, 0, places);
        this.context = context;
        this.places = places;
        this.email = email;
        this.caseNumber = caseNumber;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_place, parent, false);
        }

        Place currentPlace = places.get(position);

        // Display place details
        TextView placeNameTextView = listItem.findViewById(R.id.placeName);
        placeNameTextView.setText(currentPlace.getName());

        ImageView flagImageView = listItem.findViewById(R.id.flagImageView);

        if(Helper.checkIsNTO(currentPlace)){
            flagImageView.setVisibility(View.VISIBLE);
        } else {
            flagImageView.setVisibility(View.GONE);
        }

        TextView placeNumber = listItem.findViewById(R.id.placeNumber);
        String stringPosition = String.valueOf(position + 1);
        String placeNumberText = " "+stringPosition+") ";
        placeNumber.setText(placeNumberText);

        // Display heart button
        ImageButton favouriteButton = listItem.findViewById(R.id.favouriteButton);
        //favouriteButton.setVisibility(View.GONE); //added!!!
        if (currentPlace.getIsFavourite()) {
            favouriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            favouriteButton.setImageResource(R.drawable.ic_favorite_border);
        }

        Log.d("Firestore", "placeId: " + currentPlace.getId());

        // Handle click events for heart button
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle favourite status
                currentPlace.setIsFavourite(!currentPlace.getIsFavourite());
                QueryLocator.updateFavouriteStatus(currentPlace.getId(), currentPlace.getIsFavourite());

                // Update heart button icon
                if (currentPlace.getIsFavourite()) {
                    favouriteButton.setImageResource(R.drawable.ic_favorite_filled);
                    // Update favourite status in database (you need to implement this)
                    // Example: firestore.collection("places").document(currentPlace.getId()).update("isFavourite", true);
                } else {
                    favouriteButton.setImageResource(R.drawable.ic_favorite_border);
                    // Update favourite status in database (you need to implement this)
                    // Example: firestore.collection("places").document(currentPlace.getId()).update("isFavourite", false);
                }
            }
        });

        // Set OnClickListener for the whole list item
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PlaceView activity
                Intent placeViewIntent = new Intent(context, PlaceView.class);
                placeViewIntent.putExtra("placeId", currentPlace.getId());
                placeViewIntent.putExtra("caseNumber", caseNumber);
                placeViewIntent.putExtra("email", email);
                context.startActivity(placeViewIntent);
            }
        });

        return listItem;
    }
}
