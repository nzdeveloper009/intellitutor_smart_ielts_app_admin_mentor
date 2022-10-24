package com.fyp.intellitutor_smartieltsapp.model;

public class AdminModel {

    String address,gmail,name,password,phoneNo,profileUri,username;
    public AdminModel() {
    }

    public AdminModel(String address, String gmail, String name, String password, String phoneNo, String profileUri, String username) {
        this.address = address;
        this.gmail = gmail;
        this.name = name;
        this.password = password;
        this.phoneNo = phoneNo;
        this.profileUri = profileUri;
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
