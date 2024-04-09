package com.example.turisticheska_knizhka;

public class User {
    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean rememberMe;
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
        setRememberMe(false); // Default value
        setNotifications(true); // Default value
        setLoginFirst(true); // Default value
        setPoints(0);
    }

    // Getter and setter methods...

    // Setter and getter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Setter and getter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Setter and getter for phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // Setter and getter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Setter and getter for rememberMe
    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    // Setter and getter for notifications
    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    // Setter and getter for loginFirst
    public boolean isLoginFirst() {
        return loginFirst;
    }

    public void setLoginFirst(boolean loginFirst) {
        this.loginFirst = loginFirst;
    }
}
