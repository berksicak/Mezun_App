package com.example.myapplication;

public class ImageUploader {
    private String name;
    private String imageUrl;

    public ImageUploader(String name, String imageUrl) {
        if(name.trim().equals("")){
            name = "No Name";
        }
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public ImageUploader() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
