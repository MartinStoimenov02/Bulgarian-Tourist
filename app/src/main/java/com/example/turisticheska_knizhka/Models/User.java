package com.example.turisticheska_knizhka.Models;

public class User {
    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean notifications;
    private boolean loginFirst;
    private int points;

    // Default constructor required for Firestore
    public User() {
    }

    public User(String name, String email, String phone, String password) {
        setName(name);
        setEmail(email);
        setPhone(phone);
        setPassword(password);
        setNotifications(true); // Default value
        setLoginFirst(true); // Default value
        setPoints(0);
    }

    // Getter and setter methods...

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public int getPoints() {
        return points;
    }
    public String getPassword() {
        return password;
    }
    public boolean getLoginFirst() {
        return loginFirst;
    }
    public boolean getNotifications() {
        return notifications;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
    public void setLoginFirst(boolean loginFirst) {
        this.loginFirst = loginFirst;
    }
}
