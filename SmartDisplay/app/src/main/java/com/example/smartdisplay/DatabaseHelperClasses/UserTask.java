package com.example.smartdisplay.DatabaseHelperClasses;

public class UserTask {

    private String title;
    private String description;
    private String goal;
    private String repeatInfo;
    private Boolean repeatType;//Eğer true ise haftalık false ise birkere
    private String time;
    private Boolean alertType;//Eğer true ise default false ise video seçimi
    private String videoUrl;

    public UserTask(String title, String description, String goal, String repeatInfo, Boolean repeatType, String time, Boolean alertType, String videoUrl) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoal() {
        return goal;
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
