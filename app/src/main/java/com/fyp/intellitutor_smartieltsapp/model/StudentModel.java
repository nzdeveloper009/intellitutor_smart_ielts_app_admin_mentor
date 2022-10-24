package com.fyp.intellitutor_smartieltsapp.model;

public class StudentModel {
    String name,username,gmail,password,uid,role,mentorUid,online;

    public StudentModel() {
    }

    public StudentModel(String name, String username, String gmail, String password, String uid, String role, String mentorUid) {
        this.name = name;
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.uid = uid;
        this.role = role;
        this.mentorUid = mentorUid;
    }

    public StudentModel(String name, String username, String gmail, String password, String uid, String role, String mentorUid,String online) {
        this.name = name;
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.uid = uid;
        this.role = role;
        this.mentorUid = mentorUid;
        this.online = online;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMentorUid() {
        return mentorUid;
    }

    public void setMentorUid(String mentorUid) {
        this.mentorUid = mentorUid;
    }
}
