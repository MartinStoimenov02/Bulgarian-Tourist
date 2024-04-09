package com.example.turisticheska_knizhka;

import java.util.List;

public interface PlacesCallback {
    void onPlacesLoaded(List<Place> places);
    void onError(Exception e);
}
