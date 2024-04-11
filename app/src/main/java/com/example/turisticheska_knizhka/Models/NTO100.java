package com.example.turisticheska_knizhka.Models;

public class NTO100 {
    private String id;
    private String name;
    private String urlMap;
    private String imgPath;
    private int distance;
    private String numberInNationalList;
    private String description;
    private boolean isActive;
    public NTO100(){}
    public NTO100(String name, String urlMap, String imgPath, int distance, String numberInNationalList, String description){
        setName(name);
        setUrlMap(urlMap);
        setImgPath(imgPath);
        setDistance(distance);
        setNumberInNationalList(numberInNationalList);
        setDescription(description);
        setIsActive(true);
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
    public String getImgPath() {
        return imgPath;
    }
    public int getDistance() {
        return distance;
    }
    public String getNumberInNationalList() {
        return numberInNationalList;
    }
    public String getDescription() {
        return description;
    }
    public boolean getIsActive() {
        return isActive;
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
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public void setNumberInNationalList(String numberInNationalList) {this.numberInNationalList = numberInNationalList;}
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
