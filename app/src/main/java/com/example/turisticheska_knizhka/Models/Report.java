package com.example.turisticheska_knizhka.Models;

import com.google.firebase.firestore.DocumentReference;

public class Report {
    private String title;
    private String reportBody;
    private int grade;
    private DocumentReference userRef;

    public Report(String title, String reportBody, int grade, DocumentReference userRef){
        setTitle(title);
        setReportBody(reportBody);
        setGrade(grade);
        setUserRef(userRef);
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getReportBody() {
        return reportBody;
    }

    public int getGrade() {
        return grade;
    }

    public DocumentReference getUserRef() {
        return userRef;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setReportBody(String reportBody) {
        this.reportBody = reportBody;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setUserRef(DocumentReference userRef) {
        this.userRef = userRef;
    }
}
