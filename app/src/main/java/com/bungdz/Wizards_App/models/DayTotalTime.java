package com.bungdz.Wizards_App.models;

public class DayTotalTime {
    private String date;
    private long totalTimeMillis;

    public DayTotalTime(String date) {
        this.date = date;
        this.totalTimeMillis = 0;
    }

    public String getDate() {
        return date;
    }

    public long getTotalTimeMillis() {
        return totalTimeMillis;
    }

    public void addTime(long timeMillis) {
        totalTimeMillis += timeMillis;
    }
}
