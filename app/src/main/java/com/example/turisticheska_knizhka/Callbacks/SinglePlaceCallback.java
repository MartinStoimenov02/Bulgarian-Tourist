package com.example.turisticheska_knizhka.Callbacks;

import com.example.turisticheska_knizhka.Models.Place;

public interface SinglePlaceCallback {
    void onPlaceLoaded(Place place);
    void onError(Exception e);
}
