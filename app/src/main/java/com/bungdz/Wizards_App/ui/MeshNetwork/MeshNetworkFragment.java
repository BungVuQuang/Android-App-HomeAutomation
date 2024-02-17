package com.bungdz.Wizards_App.ui.MeshNetwork;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bungdz.Wizards_App.MainActivity;
import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Alarm;
import com.bungdz.Wizards_App.models.Device;
import com.bungdz.Wizards_App.models.NodeInfo;
import com.bungdz.Wizards_App.adapter.MeshNetworkAdapter;
import com.bungdz.Wizards_App.databinding.FragmentMeshinfoBinding;
import com.bungdz.Wizards_App.models.SharedViewModel;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.networking.ThingsboardWebSocketManager;
import com.bungdz.Wizards_App.networking.WebSocketCallback;
import com.bungdz.Wizards_App.ui.AlarmUI.CreateAlarmFragment;
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

public class MeshNetworkFragment extends Fragment {
    private FragmentMeshinfoBinding binding;
    private MeshNetworkAdapter adapter;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private List<NodeInfo> dataList;
    private List<Pair<String, String>> keyValuePairList;
    private ThingsboardWebSocketManager webSocketManager;
    private int isFirstRecei = 0;

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
        binding = FragmentMeshinfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataList = new ArrayList<>();
        ArrayList<String> listRoleActivity = ((MainActivity) requireActivity()).sharedViewModel.getListRole();
        adapter = new MeshNetworkAdapter(dataList,getContext(),listRoleActivity);
        binding.recyclerView.setAdapter(adapter);
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<String> listRole = new ArrayList<>();
        keyValuePairList = new ArrayList<>();
        WebSocketCallback callback = new WebSocketCallback() {

            @Override
            public void onWebSocketConnected() {
                webSocketManager.registerAttributesListen(Constants.THINGS_BOARD_INFOS[0].getDeviceID(),10);
            }

            @Override
            public void onWebSocketMessage(String message) {
                getActivity().runOnUiThread(() -> {
                    keyValuePairList = adapter.getKeyValuePairList();

                    try {
                        JsonElement jsonElement = JsonParser.parseString(message);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonObject dataObject = jsonObject.getAsJsonObject("data");
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
                                    String rolevalue = innerJsonObject.get("role").getAsString();
                                    String parentValue = innerJsonObject.get("parent").getAsString();
                                    String uuidValue = innerJsonObject.get("uuid").getAsString();
                                    String unicastValue = innerJsonObject.get("unicast").getAsString();
                                    NodeInfo nodeInfo = new NodeInfo(parentValue, rolevalue,unicastValue, uuidValue);
                                    if(isFirstRecei == 0){
                                        dataList.add(nodeInfo);
                                    }else{
                                        ((MainActivity) requireActivity()).sharedViewModel.addRole(rolevalue);
                                        uiHandler.post(() -> adapter.addMesh(nodeInfo));
                                    }
                                }
                            }
                            if(isFirstRecei == 0){
                                uiHandler.post(() -> {
                                    isFirstRecei=1;
                                    adapter.notifyDataSetChanged();
                                    binding.progressBar.setVisibility(View.GONE);
                                });
                            }
                        }else {
                            if (listRoleActivity.contains(keyNode)) {
                                Log.d("listRoleActivity",listRoleActivity.toString());
                                listRoleActivity.remove(keyNode);
                                keyValuePairList = adapter.getKeyValuePairList();
                                String position = findValueByKey(keyNode);
                                if(((MainActivity) requireActivity()).sharedViewModel.getListRole().contains(keyNode)){
                                    ((MainActivity) requireActivity()).sharedViewModel.removeRole(keyNode);
                                    uiHandler.post(() -> adapter.deleteMesh(Integer.parseInt(position)));
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

//        ThingsBoardHandle.getMesh(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[0].getDeviceID(), new Callback() {
//            private String responseData;
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                dataList.clear();
//                responseData = response.body().string();
//                response.body().close();
//                JsonArray jsonArray = JsonParser.parseString(responseData).getAsJsonArray();
//                for (int i = 0; i < jsonArray.size(); i++) {
//                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
//                    String key = jsonObject.get("key").getAsString();
//                    JsonObject value = jsonObject.get("value").getAsJsonObject();
//                    listRole.add(value.get("role").getAsString());
//                    NodeInfo nodeInfo = new NodeInfo(value.get("parent").getAsString(), value.get("role").getAsString(),value.get("unicast").getAsString(),value.get("uuid").getAsString());
//                    dataList.add(nodeInfo);
//                }
//                uiHandler.post(() -> {
//
//                    adapter.notifyDataSetChanged();
//                    binding.progressBar.setVisibility(View.GONE);
//                });
//            }
//        });
//        Handler handler = new Handler();
//        Runnable runnableCode = new Runnable() {
//            @Override
//            public void run() {
//                Log.d("Runnable","Đã chạy");
//                ThingsBoardHandle.getUpdateMesh(Constants.THINGS_BOARD_INFOS[0].getAccessToken(), new Callback() {
//                private String responseData;
//
//                @Override
//                public void onFailure(Request request, IOException e) {
//
//            }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    responseData = response.body().string();
//                    response.body().close();
//                    if(responseData.contains("deleted")){
//                        keyValuePairList = adapter.getKeyValuePairList();
//                        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
//                        if (jsonObject.has("deleted")) {
//                            JsonArray deletedArray = jsonObject.getAsJsonArray("deleted");
//                            for (int i = 0; i < deletedArray.size(); i++) {
//                                String value = deletedArray.get(i).getAsString();
//                                String position = findValueByKey(value);
//                                if(((MainActivity) requireActivity()).sharedViewModel.getListRole().contains(value)){
//                                    ((MainActivity) requireActivity()).sharedViewModel.removeRole(value);
//                                    uiHandler.post(() -> adapter.deleteMesh(Integer.parseInt(position)));
//                                }
//                            }
//                        }
//                    }else {
//                        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
//                        for (String key : jsonObject.keySet()) {
//                            JsonElement value = jsonObject.get(key);
//                            JsonObject json = value.getAsJsonObject();
//                            NodeInfo nodeInfo = new NodeInfo(json.get("parent").getAsString(), json.get("role").getAsString(), json.get("unicast").getAsString(), json.get("uuid").getAsString());
//                            ((MainActivity) requireActivity()).sharedViewModel.addRole(json.get("role").getAsString());
//                            uiHandler.post(() -> adapter.addMesh(nodeInfo));
//                        }
//                    }
//
////                    JsonArray jsonArray = JsonParser.parseString(responseData).getAsJsonArray();
////                    for (int i = 0; i < jsonArray.size(); i++) {
////                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
////                        JsonObject value = jsonObject.get("value").getAsJsonObject();
////                        NodeInfo nodeInfo = new NodeInfo(value.get("parent").getAsString(), value.get("role").getAsString(),value.get("unicast").getAsString(),value.get("uuid").getAsString());
////                        uiHandler.post(() -> {
////                            adapter.addMesh(nodeInfo);
////                        });
////                    }
//                    Log.d("getUpdateMesh",responseData);
//                }
//            });
//                handler.postDelayed(this, 40000);
//            }
//        };
//        handler.post(runnableCode);
        return root;
    }
}