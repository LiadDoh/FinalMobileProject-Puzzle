package com.example.puzzle;

public class Profile {
    private String Email, First_Name, Last_Name, Uid, imageUrl, FacebookID;
    private int Score;

    public Profile() {
    }

    public Profile(String email, String first_Name, String last_Name, int score, String uid, String userImg, String facebookID) {
        Email = email;
        First_Name = first_Name;
        Last_Name = last_Name;
        Score = score;
        Uid = uid;
        imageUrl = userImg;
        FacebookID = facebookID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFacebookID() {
        return FacebookID;
    }

    public void setFacebookID(String facebookID) {
        this.FacebookID = facebookID;
    }
}
