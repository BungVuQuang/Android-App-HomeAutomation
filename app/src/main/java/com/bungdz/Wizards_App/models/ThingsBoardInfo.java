package com.bungdz.Wizards_App.models;

public class ThingsBoardInfo {
    public static String JWT_TOKEN;
    private String accessToken;
    private String deviceID;

    public ThingsBoardInfo(String accessToken, String deviceID){
        this.accessToken = accessToken;
        this.deviceID = deviceID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public static int getIndexThingsBoard(String selectedElement) {
        switch (selectedElement) {
            case "Gateway":
                return 0;
            case "Node1":
                return 1;
            case "Node2":
                return 2;
            case "Node3":
                return 3;
            case "Node4":
                return 4;
            case "Node5":
                return 5;
            case "Node6":
                return 6;
            default:
                return -1; // Giá trị mặc định hoặc xử lý lỗi
        }
    }


}
