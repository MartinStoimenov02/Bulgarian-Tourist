package com.example.turisticheska_knizhka;

import java.util.List;

public interface NTO100Callback {
    void onNTO100Loaded(List<NTO100> nto100s);
    void onError(Exception e);
}
