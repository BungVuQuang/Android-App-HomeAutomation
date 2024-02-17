package com.bungdz.Wizards_App.models;

public class Alarm {
    private String role;
    private String id;
    private String device;
    private String date;
    private String time;
    private String state;

    public Alarm() {
        // Constructor rỗng
    }

    public Alarm(String role, String id, String device, String date, String time, String state) {
        this.role = role;
        this.id = id;
        this.device = device;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
