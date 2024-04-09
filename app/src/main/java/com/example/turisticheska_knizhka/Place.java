package com.example.turisticheska_knizhka;

import com.google.firebase.firestore.DocumentReference;

public class Place {
    private String id;
    private String name;
    private String urlMap;
    private String workingHours;
    private String placePhoneNumber;
    private String imgPath;
    private int distance;
    private boolean isFavourite;
    private boolean isVisited;
    private DocumentReference userEmail; // Changed type to DocumentReference
    private DocumentReference nto100; // Changed type to DocumentReference

    // Default constructor
    public Place() {
        // Default constructor required for Firebase deserialization
    }

    public Place(String name, String urlMap, String workingHours, String placePhoneNumber, String imgPath, int distance, DocumentReference userEmail, DocumentReference nto100) {
        setName(name);
        setUrlMap(urlMap);
        setWorkingHours(workingHours);
        setPlacePhoneNumber(placePhoneNumber);
        setImgPath(imgPath);
        setDistance(distance);
        setIsFavourite(false);
        setIsVisited(false);
        setUserEmail(userEmail);
        setNto100(nto100);
    }

    public Place(String id, String name, String urlMap, String workingHours, String placePhoneNumber, String imgPath, int distance, boolean isFavourite, boolean isVisited, DocumentReference userEmail, DocumentReference nto100) {
        setId(id);
        setName(name);
        setUrlMap(urlMap);
        setWorkingHours(workingHours);
        setPlacePhoneNumber(placePhoneNumber);
        setImgPath(imgPath);
        setDistance(distance);
        setIsFavourite(isFavourite);
        setIsVisited(isVisited);
        setUserEmail(userEmail);
        setNto100(nto100);
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

    public String getWorkingHours() {
        return workingHours;
    }

    public String getPlacePhoneNumber() {
        return placePhoneNumber;
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

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public void setPlacePhoneNumber(String placePhoneNumber) {
        this.placePhoneNumber = placePhoneNumber;
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
}