package com.example.connectus.Models;

public class ModelUser {
    // use same name as in firebase
    String cover, image,  mail, password,search,phone,userId,userName,onlineStatus;
    public ModelUser(){}

    public ModelUser(String cover, String image, String mail, String password, String search, String phone, String userId, String userName, String onlineStatus) {
        this.cover = cover;
        this.image = image;
        this.mail = mail;
        this.password = password;
        this.search = search;
        this.phone = phone;
        this.userId = userId;
        this.userName = userName;
        this.onlineStatus = onlineStatus;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
