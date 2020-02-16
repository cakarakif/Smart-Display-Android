package com.example.smartdisplay.DatabaseHelperClasses;

public class UserInformation {
    private String name;
    private String surname;
    private String birthday;

    public UserInformation(String birthday, String surname, String name) {
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
    }

    public UserInformation() {//Firebase için boş bir constructor'da olmalı.//Eğer bu tanımlamada veri yoksa buraya düşer
        this.name = "";
        this.surname = "";
        this.birthday = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
