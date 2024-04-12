package com.example.turisticheska_knizhka.Callbacks;
import com.example.turisticheska_knizhka.Models.User;

public interface SingleUserCallback {
    void onUserLoaded(User usr);
    void onError(Exception e);
}
