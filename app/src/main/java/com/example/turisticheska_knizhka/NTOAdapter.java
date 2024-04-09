package com.example.turisticheska_knizhka;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NTOAdapter extends ArrayAdapter<NTO100> {
    private Context context;
    private List<NTO100> nto100s;

    public NTOAdapter(Context context, List<NTO100> nto100s) {
        super(context, 0, nto100s);
        this.context = context;
        this.nto100s = nto100s;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_place, parent, false);
        }

        NTO100 currentPlace = nto100s.get(position);

        // Display place details
        TextView placeNameTextView = listItem.findViewById(R.id.placeName);
        placeNameTextView.setText(currentPlace.getName());

        TextView placeNumber = listItem.findViewById(R.id.placeNumber);
        String placeNumberText = " "+currentPlace.getNumberInNationalList()+") ";
        placeNumber.setText(placeNumberText);

        // Display heart button
        ImageButton favouriteButton = listItem.findViewById(R.id.favouriteButton);
        favouriteButton.setVisibility(View.GONE);

        ImageView flagImageView = listItem.findViewById(R.id.flagImageView);
        flagImageView.setVisibility(View.VISIBLE);

        Log.d("Firestore", "placeId: " + currentPlace.getId());
        return listItem;
    }
}
