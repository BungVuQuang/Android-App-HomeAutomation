package com.bungdz.Wizards_App.networking;

import android.content.Context;
import android.util.Log;

import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Alarm;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ThingsBoardHandle {

    public static MqttAndroidClient mqttAndroidClientHiveMQ;
    public ThingsBoardHandle(){

    }
    public static MqttAndroidClient ConnectMqttHiveMQ(Context mContext, String clientID, MqttConnectionCallback callback) {
        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(mContext, Constants.HiveMQ_URL, clientID);

        // Cấu hình options cho client
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setKeepAliveInterval(60); // 60 giây hoặc giá trị lớn hơn

        try {
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (callback != null) {
                        callback.onFailure(exception);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mqttAndroidClient;
    }

    public static void disconnectMqttClient(MqttAndroidClient mqttAndroidClient) {
        if (mqttAndroidClient != null) {
            if (mqttAndroidClient.isConnected()) {
                try {
                    IMqttToken disconnectToken = mqttAndroidClient.disconnect();
                    disconnectToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // Xử lý thành công khi huỷ kết nối
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Xử lý khi huỷ kết nối thất bại
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                mqttAndroidClient.unregisterResources();
                //mqttAndroidClient.close();
            }
        }
    }
public static void getLastData(String jwtToken,String deviceID, Callback callback) {
    OkHttpClient client = new OkHttpClient();

    String apiUrl = Constants.THINGSBOARD_GET_LASTDATA_URL.replace("DEVICE_ID", String.valueOf(deviceID));
    Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("X-Authorization","Bearer " + jwtToken)
            .get()
            .build();

    client.newCall(request).enqueue(callback);
}
    public static void getLastDataByKey(String jwtToken,String deviceID,String key, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_PERIOD_URL.replace("DEVICE_ID", String.valueOf(deviceID))
                .replace("KEY", String.valueOf(key));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
    public static void getAlarm(String jwtToken,String deviceID, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_ALARM_URL.replace("DEVICE_ID", String.valueOf(deviceID));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getAllKeysAlarm(String jwtToken,String deviceID, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_ALL_KEYS_AlARM_URL.replace("DEVICE_ID", String.valueOf(deviceID));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void setAlarm(Alarm alarm, String jwtToken, String deviceID, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = String.format("{\"%s\":{\"date\": \"%s\", \"device\": \"%s\",\"time\": \"%s\", \"state\": \"%s\"}}", alarm.getId(), alarm.getDate(), alarm.getDevice(), alarm.getTime(), alarm.getState());
        RequestBody requestBody = RequestBody.create(JSON, json);
        String apiUrl = Constants.THINGSBOARD_POST_AlARM_URL.replace("DEVICE_ID", String.valueOf(deviceID));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }


    public static void deleteAlarm(String jwtToken,String deviceID,String keys, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_DELETE_AlARM_URL.replace("DEVICE_ID", String.valueOf(deviceID))
                .replace("KEY", keys);;
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .delete()
                .build();

        client.newCall(request).enqueue(callback);
    }
    public static void getUpdateAlarm(String accessToken, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_UPDATE_AlARM_URL.replace("ACCESS_TOKEN", String.valueOf(accessToken));
        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getActiveGateway(String jwtToken, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_ACTIVE_URL;
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getMesh(String jwtToken,String deviceID, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_MESH_URL.replace("DEVICE_ID", String.valueOf(deviceID));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void deleteMesh(String jwtToken,String deviceID,String keys, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_DELETE_MESH_URL.replace("DEVICE_ID", String.valueOf(deviceID))
                .replace("KEY", keys);;
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .delete()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getUpdateMesh(String accessToken, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_UPDATE_MESH_URL.replace("ACCESS_TOKEN", String.valueOf(accessToken));
        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }


    public static void getFieldData(String jwtToken,String deviceID, String field,long startTS, long endTS, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = Constants.THINGSBOARD_GET_TIMESERIES_URL.replace("DEVICE_ID", String.valueOf(deviceID))
                .replace("FIELD", String.valueOf(field))
                .replace("TIME_START", String.valueOf(startTS))
                .replace("TIME_END",String.valueOf(endTS));
        Log.d("getFieldData", apiUrl);
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void httpPostTelemetryData(String jwtToken, String deviceID, String deviceName, String value, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String apiUrl = Constants.THINGSBOARD_POST_API.replace("DEVICE_ID", String.valueOf(deviceID));
        String json = String.format("{\"%s\":%s}", deviceName, value);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void httpDeleteTelematryData(String jwtToken, String deviceID, String deviceName, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = Constants.THINGSBOARD_DELETE_API.replace("DEVICE_ID", String.valueOf(deviceID))
                .replace("FIELD", String.valueOf(deviceName));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("accept","application/json")
                .addHeader("X-Authorization","Bearer " + jwtToken)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getjwtToken(String userTB, String passwordTB, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", userTB, passwordTB);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(Constants.THINGSBOARD_URL_LOGIN)
                .addHeader("accept","application/json")
                .addHeader("Content-Type","application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
