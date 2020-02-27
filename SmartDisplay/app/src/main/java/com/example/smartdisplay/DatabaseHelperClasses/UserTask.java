package com.example.smartdisplay.DatabaseHelperClasses;

import java.io.Serializable;

public class UserTask implements Serializable {

    private Boolean isActive;
    private String id;
    private String title;
    private String description;
    private String goal;
    private String repeatInfo;
    private Boolean repeatType;//Eğer true ise haftalık false ise birkere
    private String time;
    private Boolean alertType;//Eğer true ise default false ise video seçimi
    private String videoUrl;

    public UserTask(Boolean isActive,String title, String description, String goal, String repeatInfo, Boolean repeatType, String time, Boolean alertType, String videoUrl, String id) {
        this.isActive=isActive;
        this.id=id;
        this.title = title;
        this.description = description;
        this.goal = goal;
        this.repeatInfo = repeatInfo;
        this.repeatType = repeatType;
        this.time = time;
        this.alertType = alertType;
        this.videoUrl = videoUrl;
    }

    public UserTask() {
        this.title = "";
        this.description = "";
        this.goal = "";
        this.repeatInfo = "";
        this.repeatType = false;
        this.time = "";
        this.alertType = false;
        this.videoUrl = "";
    }


    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {

        return description.equals("") ? "No description":description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoal() {
        return goal.equals("") ? "0":goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getRepeatInfo() {
        return repeatInfo;
    }

    public void setRepeatInfo(String repeatInfo) {
        this.repeatInfo = repeatInfo;
    }

    public Boolean getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(Boolean repeatType) {
        this.repeatType = repeatType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getAlertType() {
        return alertType;
    }

    public void setAlertType(Boolean alertType) {
        this.alertType = alertType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
