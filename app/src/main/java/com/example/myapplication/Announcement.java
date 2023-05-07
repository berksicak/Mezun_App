package com.example.myapplication;


import java.util.ArrayList;

public class Announcement {
    private String description;
    private String  dateTime;
    private String userName;
    private String expireDate;
    private String image;

    public Announcement(String description, String dateTime, String userName, String expireDate) {
        this.description = description;
        this.dateTime = dateTime;
        this.userName = userName;
        this.expireDate = expireDate;
    }

    public Announcement(String description) {
        this.description = description;
    }

    public static ArrayList<Announcement> setData(){
        ArrayList<Announcement> list = new ArrayList<>();

        return list;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
