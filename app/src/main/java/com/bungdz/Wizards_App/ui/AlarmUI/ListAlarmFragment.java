package com.bungdz.Wizards_App.ui.AlarmUI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bungdz.Wizards_App.MainActivity;
import com.bungdz.Wizards_App.R;
import com.bungdz.Wizards_App.adapter.AlarmAdapter;
import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Alarm;
import com.bungdz.Wizards_App.models.Device;
import com.bungdz.Wizards_App.models.NodeInfo;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.databinding.FragmentListAlarmBinding;
import com.bungdz.Wizards_App.networking.ThingsboardWebSocketManager;
import com.bungdz.Wizards_App.networking.WebSocketCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListAlarmFragment extends Fragment {
    private FragmentListAlarmBinding binding;
    private AlarmAdapter adapter;
    private List<Pair<String, String>> keyValuePairList;
    private ThingsboardWebSocketManager webSocketManager;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private int isFirstRecei = 0;
    private List<Alarm> dataList;

    private String findValueByKey(String key) {
        for (Pair<String, String> keyValuePair : keyValuePairList) {
            String currentKey = keyValuePair.first;
            String value = keyValuePair.second;
            if (currentKey.equals(key)) {
                return value;
            }
        }
        return null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListAlarmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerViewAlarmList.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataList = new ArrayList<>();
        adapter = new AlarmAdapter(dataList, ThingsBoardHandle.mqttAndroidClientHiveMQ);
        binding.recyclerViewAlarmList.setAdapter(adapter);
        keyValuePairList = new ArrayList<>();
        WebSocketCallback callback = new WebSocketCallback() {
            private String position;
            @Override
            public void onWebSocketConnected() {
                for(int index = 11; index < 10 + Constants.THINGS_BOARD_INFOS.length; index++){
                    webSocketManager.registerAttributesListen(Constants.THINGS_BOARD_INFOS[index - 10].getDeviceID(),index);
                }
            }

            @Override
            public void onWebSocketMessage(String message) {
                getActivity().runOnUiThread(() -> {
                    try {
                        JsonElement jsonElement = JsonParser.parseString(message);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonObject dataObject = jsonObject.getAsJsonObject("data");
                        if (dataObject.entrySet().isEmpty()) {

                        } else {
                            JsonObject latestValuesObject = jsonObject.getAsJsonObject("latestValues");
                            Map.Entry<String, JsonElement> entry = latestValuesObject.entrySet().iterator().next();
                            String keyNode = entry.getKey();
                            JsonElement valueElement = entry.getValue();
                            if(Long.parseLong(valueElement.toString()) != 0){
                                for (String key : dataObject.keySet()) {
                                    JsonArray dataArray = dataObject.getAsJsonArray(key);
                                    for (JsonElement element : dataArray) {
                                        JsonArray innerArray = element.getAsJsonArray();
                                        String innerJsonString = innerArray.get(1).getAsString();
                                        JsonObject innerJsonObject = JsonParser.parseString(innerJsonString).getAsJsonObject();
                                        String[] roleSplit = key.split("_");
                                        String rolevalue = roleSplit[0];
                                        String idValue = key;
                                        String deviceValue = innerJsonObject.get("device").getAsString();
                                        String dateValue = innerJsonObject.get("date").getAsString();
                                        String timeValue = innerJsonObject.get("time").getAsString();
                                        String stateValue = innerJsonObject.get("state").getAsString();
                                        Alarm alarm = new Alarm(rolevalue, idValue,deviceValue,dateValue, timeValue, stateValue);
                                        if(isFirstRecei == 0){
                                            dataList.add(alarm);
                                        }else{
                                            uiHandler.post(() -> adapter.addAlarm(alarm));
                                        }
                                    }
                                }
                                if(isFirstRecei == 0){
                                    uiHandler.post(() -> {
                                        isFirstRecei=1;
                                        adapter.notifyDataSetChanged();
                                    });
                                }
                            }else {
                                keyValuePairList = adapter.getKeyValuePairList();
                                String position = findValueByKey(keyNode);
                                Log.d("List alarm",keyNode + " : "  + position);
                                if(position !=null){
                                    uiHandler.post(() -> adapter.deleteAlarm(Integer.parseInt(position)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onWebSocketClosed(int code, String reason) {

            }

            @Override
            public void onWebSocketError(Exception ex) {

            }
        };
        try {
            webSocketManager = new ThingsboardWebSocketManager(ThingsBoardInfo.JWT_TOKEN, callback);
            webSocketManager.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        ThingsBoardHandle.getAlarm(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[1].getDeviceID(), new Callback() {
//            private String responseData;
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                responseData = response.body().string();
//                response.body().close();
//                JsonArray jsonArray = JsonParser.parseString(responseData).getAsJsonArray();
//                for (int i = 0; i < jsonArray.size(); i++) {
//                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
//                    JsonObject value = jsonObject.get("value").getAsJsonObject();
//                    Alarm alarm = new Alarm(value.get("role").getAsString(), value.get("id").getAsString(),value.get("device").getAsString(),value.get("date").getAsString(), value.get("time").getAsString());
//                    dataList.add(alarm);
//                }
//                uiHandler.post(() -> {
//                    adapter.notifyDataSetChanged();
//                });
//            }
//        });

//        Handler handler = new Handler();
//        Runnable runnableCode = new Runnable() {
//            @Override
//            public void run() {
//                Log.d("RunnableAlarm","Đã chạy");
//                ThingsBoardHandle.getUpdateAlarm(Constants.THINGS_BOARD_INFOS[1].getAccessToken(), new Callback() {
//                    private String responseData;
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        responseData = response.body().string();
//                        response.body().close();
//                        Log.d("RunnableAlarm",responseData);
//                        keyValuePairList = adapter.getKeyValuePairList();
//                        if(responseData.contains("deleted")){
//                            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
//                            if (jsonObject.has("deleted")) {
//                                JsonArray deletedArray = jsonObject.getAsJsonArray("deleted");
//                                for (int i = 0; i < deletedArray.size(); i++) {
//                                    String value = deletedArray.get(i).getAsString();
//                                    String position = findValueByKey(value);
//                                    if(position !=null){
//                                        uiHandler.post(() -> adapter.deleteAlarm(Integer.parseInt(position)));
//                                    }
//                                }
//                            }
//                        }else{
//                            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
//                            for (String key : jsonObject.keySet()) {
//                                JsonElement value = jsonObject.get(key);
//                                JsonObject json = value.getAsJsonObject();
//                                Alarm alarm = new Alarm(json.get("role").getAsString(), json.get("id").getAsString(),json.get("device").getAsString(),json.get("date").getAsString(), json.get("time").getAsString());
//
//                                uiHandler.post(() -> adapter.addAlarm(alarm));
//                            }
//                        }
//
//                    }
//                });
//                handler.postDelayed(this, 40000);
//            }
//        };
//        handler.post(runnableCode);
        return root;

    }
}

