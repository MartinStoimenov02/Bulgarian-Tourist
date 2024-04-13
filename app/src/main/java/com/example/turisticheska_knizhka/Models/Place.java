package com.example.turisticheska_knizhka.Models;

import com.google.firebase.firestore.DocumentReference;

public class Place {
    private String id;
    private String name;
    private String urlMap;
    private String imgPath;
    private int distance;
    private boolean isFavourite;
    private boolean isVisited;
    private DocumentReference userEmail;
    private DocumentReference nto100;
    private String description;

    // Default constructor
    public Place() {}

    public Place(String name, String urlMap, String imgPath, int distance, DocumentReference userEmail, DocumentReference nto100, String description) {
        setName(name);
        setUrlMap(urlMap);
        setImgPath(imgPath);
        setDistance(distance);
        setIsFavourite(false);
        setIsVisited(false);
        setUserEmail(userEmail);
        setNto100(nto100);
        setDescription(description);
    }

    // Getters
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getUrlMap() {
        return urlMap;
    }
    public String getImgPath() {
        return imgPath;
    }
    public int getDistance() {
        return distance;
    }
    public boolean getIsFavourite() {
        return isFavourite;
    }
    public boolean getIsVisited() {
        return isVisited;
    }
    public DocumentReference getUserEmail() {
        return userEmail;
    }
    public DocumentReference getNto100() {
        return nto100;
    }
    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUrlMap(String urlMap) {
        this.urlMap = urlMap;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public void setIsFavourite(boolean favourite) {
        isFavourite = favourite;
    }
    public void setIsVisited(boolean visited) {
        isVisited = visited;
    }
    public void setUserEmail(DocumentReference userEmail) {
        this.userEmail = userEmail;
    }
    public void setNto100(DocumentReference nto100) {
        this.nto100 = nto100;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}