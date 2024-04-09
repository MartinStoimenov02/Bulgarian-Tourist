package com.example.turisticheska_knizhka;

public interface SinglePlaceCallback {
    void onPlaceLoaded(Place place);
    void onError(Exception e);
}
