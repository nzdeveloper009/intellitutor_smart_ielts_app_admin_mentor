package com.fyp.intellitutor_smartieltsapp.model;

public class MentorModel {
    String name,age,qualification,institudeName,username,gmail,password,nicCardImage,packageName,uid,online;
    String role , countStudents;

    public MentorModel() {
    }


    public MentorModel(String name, String age, String qualification, String institudeName, String username, String gmail, String password, String nicCardImage, String packageName, String uid, String online,String role) {
        this.name = name;
        this.age = age;
        this.qualification = qualification;
        this.institudeName = institudeName;
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.nicCardImage = nicCardImage;
        this.packageName = packageName;
        this.uid = uid;
        this.online = online;
        this.role = role;
    }

    public MentorModel(String name, String institudeName, String username, String gmail, String password, String packageName, String uid, String online, String role) {
        this.name = name;
        this.institudeName = institudeName;
        this.username = username;
        this.gmail = gmail;
        this.password = password;
        this.packageName = packageName;
        this.uid = uid;
        this.online = online;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCountStudents() {
        return countStudents;
    }

    public void setCountStudents(String countStudents) {
        this.countStudents = countStudents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getInstitudeName() {
        return institudeName;
    }

    public void setInstitudeName(String institudeName) {
        this.institudeName = institudeName;
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

    public String getNicCardImage() {
        return nicCardImage;
    }

    public void setNicCardImage(String nicCardImage) {
        this.nicCardImage = nicCardImage;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
