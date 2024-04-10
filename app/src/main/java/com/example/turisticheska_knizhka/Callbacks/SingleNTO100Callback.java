package com.example.turisticheska_knizhka.Callbacks;

import com.example.turisticheska_knizhka.Models.NTO100;

public interface SingleNTO100Callback {
    void onNTOLoaded(NTO100 nto100);
    void onError(Exception e);
}
