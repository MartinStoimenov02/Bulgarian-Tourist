package com.example.turisticheska_knizhka.Callbacks;

import com.example.turisticheska_knizhka.Models.Place;

import java.util.List;

public interface PlacesCallback {
    void onPlacesLoaded(List<Place> places);
    void onError(Exception e);
}
