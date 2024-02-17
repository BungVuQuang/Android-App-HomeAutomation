package com.bungdz.Wizards_App.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DataPoint {
    private String date;
    private String time;
    private double value;

    public DataPoint(String date, String time, double value) {
        this.date = date;
        this.time = time;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public static List<DataPoint> parseDataFromResponseTB(String deviceName ,String jsonData) {
        List<DataPoint> dataEntries = new ArrayList<>();

        JsonArray jsonArray = JsonParser.parseString(jsonData).getAsJsonObject().getAsJsonArray(deviceName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));

        for (int i = jsonArray.size() - 1; i >= 0; i--) {
            JsonObject entryObject = jsonArray.get(i).getAsJsonObject();
            long timestamp = entryObject.get("ts").getAsLong();
            double value = entryObject.get("value").getAsDouble();
            Date date = new Date(timestamp);
            DataPoint dataPoint = new DataPoint(dateFormat.format(date), timeFormat.format(date), value);
            dataEntries.add(dataPoint);
        }
        return dataEntries;
    }
}
