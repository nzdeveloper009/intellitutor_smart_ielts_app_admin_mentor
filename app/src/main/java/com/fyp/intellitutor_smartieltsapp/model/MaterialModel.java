package com.fyp.intellitutor_smartieltsapp.model;

public class MaterialModel {
    String category,filePath,longDesc,name,shortDesc,title,type,uploadBy,timestamp;
    String key;
    public MaterialModel() {
    }

    public MaterialModel(String category, String filePath, String longDesc, String name, String shortDesc, String title, String type, String uploadBy, String timestamp) {
        this.category = category;
        this.filePath = filePath;
        this.longDesc = longDesc;
        this.name = name;
        this.shortDesc = shortDesc;
        this.title = title;
        this.type = type;
        this.uploadBy = uploadBy;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
