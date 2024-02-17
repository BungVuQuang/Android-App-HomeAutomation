package com.bungdz.Wizards_App.networking;

import com.bungdz.Wizards_App.constants.Constants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingsboardWebSocketManager {
    private WebSocketClient webSocketClient;
    public ThingsboardWebSocketManager(String jwtToken, WebSocketCallback callback) throws URISyntaxException {
        URI uri = new URI(Constants.WS_URL + "?token=" + jwtToken);
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Đã kết nối websocket thành công!! ");
//                registerListen(deviceId,0);
//                registerListen(ThingsBoardInfo.THINGS_BOARD_INFOS[0].getDeviceID(),1);
                callback.onWebSocketConnected();
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received message: " + message);
                callback.onWebSocketMessage(message);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("kết nối websocket thất bại!! "+code + " " + reason);
                callback.onWebSocketClosed(code, reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                System.out.println("WebSocket error: " + ex.getMessage());
                callback.onWebSocketError(ex);
            }
        };
    }

    public static int getSubscriptionId(String receivedMessage) {
        try {
            JSONObject jsonObject = new JSONObject(receivedMessage);
            return jsonObject.getInt("subscriptionId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1; // Hoặc giá trị khác tùy thuộc vào trường hợp của bạn
    }

    public static List<Map.Entry<String, String>> extractKeyValues(String receivedMessage) {
        List<Map.Entry<String, String>> keyValues = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(receivedMessage);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray keys = dataObject.names();
            if (keys != null) {
                for (int i = 0; i < keys.length(); i++) {
                    String key = keys.getString(i);
                    JSONArray valueArray = dataObject.getJSONArray(key);
                    if (valueArray.length() > 0) {
                        JSONArray dataPoint = valueArray.getJSONArray(0);
                        String value = dataPoint.getString(1);
                        keyValues.add(new HashMap.SimpleEntry<>(key, value));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keyValues;
    }

    public void registerListen( String entityId, int cmdID) {
        if (webSocketClient.isOpen()) {
            JSONObject object = new JSONObject();
            try {
                JSONArray tsSubCmds = new JSONArray();
                JSONObject tsSubCmd = new JSONObject();
                tsSubCmd.put("entityType", "DEVICE");
                tsSubCmd.put("entityId", entityId);
                tsSubCmd.put("scope", "LASTEST_TELEMETRY");
                tsSubCmd.put("cmdId", cmdID);
                tsSubCmd.put("keys", "");
                tsSubCmds.put(tsSubCmd);
                object.put("tsSubCmds", tsSubCmds);

                JSONArray historyCmds = new JSONArray();
                object.put("historyCmds", historyCmds);

                JSONArray attrSubCmds = new JSONArray();
                object.put("attrSubCmds", attrSubCmds);

                String data = object.toString();
                webSocketClient.send(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerAttributesListen( String entityId, int cmdID) {
        if (webSocketClient.isOpen()) {
            JSONObject object = new JSONObject();
            try {
                JSONArray tsSubCmds = new JSONArray();
                object.put("tsSubCmds", tsSubCmds);

                JSONArray historyCmds = new JSONArray();
                object.put("historyCmds", historyCmds);

                JSONArray attrSubCmds = new JSONArray();
                JSONObject attrSubCmd = new JSONObject();
                attrSubCmd.put("entityType", "DEVICE");
                attrSubCmd.put("entityId", entityId);
                attrSubCmd.put("scope", "SHARED_SCOPE");
                attrSubCmd.put("cmdId", cmdID);
                attrSubCmd.put("unsubscribe", false);
                attrSubCmd.put("keys", "");
                attrSubCmds.put(attrSubCmd);
                object.put("attrSubCmds", attrSubCmds);

                String data = object.toString();
                webSocketClient.send(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void connect() {
        webSocketClient.connect();
    }

    public void disconnect() {
        webSocketClient.close();
    }
}
