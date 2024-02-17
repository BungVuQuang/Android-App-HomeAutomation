package com.bungdz.Wizards_App.networking;

public interface MqttConnectionCallback {
    void onSuccess();

    void onFailure(Throwable exception);
}
