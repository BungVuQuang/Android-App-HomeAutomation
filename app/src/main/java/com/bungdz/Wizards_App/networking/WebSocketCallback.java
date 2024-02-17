package com.bungdz.Wizards_App.networking;

public interface WebSocketCallback {
    void onWebSocketConnected();
    void onWebSocketMessage(String message);
    void onWebSocketClosed(int code, String reason);
    void onWebSocketError(Exception ex);
}
