package com.example.myapplication;

import android.graphics.Bitmap;

import java.time.LocalDate;

public class User {
    private String userID;
    private String name;
    private String surname;
    private String startDate;
    private String gradulationYear;
    private String email;
    private String password;
    private Bitmap image;
    private String educationLevel;
    private String phoneNo;

    public User(String id, String name, String surname, String startDate, String gradulationYear, String email, String password, Bitmap image) {
        this.userID = id;
        this.name = name;
        this.surname = surname;
        this.startDate = startDate;
        this.gradulationYear = gradulationYear;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public User(String userID, String name, String surname, String startDate, String gradulationYear, String email, String password, String educationLevel, String phoneNo) {
        this.userID = userID;
        this.name = name;
        this.surname = surname;
        this.startDate = startDate;
        this.gradulationYear = gradulationYear;
        this.email = email;
        this.password = password;
        this.educationLevel = educationLevel;
        this.phoneNo = phoneNo;
    }

    public User(String userID, String name, String surname, String startDate, String gradulationYear, String email, String password, Bitmap image, String educationLevel, String phoneNo) {
        this.userID = userID;
        this.name = name;
        this.surname = surname;
        this.startDate = startDate;
        this.gradulationYear = gradulationYear;
        this.email = email;
        this.password = password;
        this.image = image;
        this.educationLevel = educationLevel;
        this.phoneNo = phoneNo;
    }

    public User(String name){
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getGradulationYear() {
        return gradulationYear;
    }

    public void setGradulationYear(String gradulationYear) {
        this.gradulationYear = gradulationYear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
