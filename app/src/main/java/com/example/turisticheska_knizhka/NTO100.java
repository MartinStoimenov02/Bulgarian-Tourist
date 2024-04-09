package com.example.turisticheska_knizhka;

public class NTO100 {
    public String id;
    public String name;
    public String urlMap;
    public String workingHours;
    public String placePhoneNumber;
    public String imgPath;
    public int distance;
    public String numberInNationalList;
    public NTO100(){}
    public NTO100(String name, String urlMap, String workingHours, String placePhoneNumber, String imgPath, int distance, String numberInNationalList){
        this.name = name;
        this.urlMap = urlMap;
        this.workingHours = workingHours;
        this.placePhoneNumber = placePhoneNumber;
        this.imgPath = imgPath;
        this.distance = distance;
        this.numberInNationalList = numberInNationalList;
    }

    public NTO100(String id, String name, String urlMap, String workingHours, String placePhoneNumber, String imgPath, int distance, String numberInNationalList){
        this.id = id;
        this.name = name;
        this.urlMap = urlMap;
        this.workingHours = workingHours;
        this.placePhoneNumber = placePhoneNumber;
        this.imgPath = imgPath;
        this.distance = distance;
        this.numberInNationalList = numberInNationalList;
    }

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

    public String getNumberInNationalList() {
        return numberInNationalList;
    }

    // Setter methods
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

    public void setNumberInNationalList(String numberInNationalList) {
        this.numberInNationalList = numberInNationalList;
    }
}
