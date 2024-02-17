package com.bungdz.Wizards_App.models;

public class DayAverageValue {
    private String date;
    private float averageValue;
    private int count;

    public DayAverageValue(String date, float averageValue) {
        this.date = date;
        this.averageValue = averageValue;
        this.count = 1;
    }

    public String getDate() {
        return date;
    }

    public float getAverageValue() {
        return averageValue;
    }

    public void addValue(float value) {
        averageValue = (averageValue * count + value) / (count + 1);
        count++;
    }
}
