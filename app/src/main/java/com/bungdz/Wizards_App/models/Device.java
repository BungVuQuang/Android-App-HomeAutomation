package com.bungdz.Wizards_App.models;
public class Device {
    private String deviceName;
    private String device;

    private String value;

    public Device(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDevice(String device) {
        this.device = device;
    }

    public String getDevice() {
        return device;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getRole(int index) {
        switch (index) {
            case 0:
                return "Gateway";
            case 1:
                return "Node1";
            case 2:
                return "Node2";
            case 3:
                return "Node3";
            case 4:
                return "Node4";
            case 5:
                return "Node5";
            case 6:
                return "Node6";
            default:
                return null; // Giá trị mặc định hoặc xử lý lỗi
        }
    }
}

