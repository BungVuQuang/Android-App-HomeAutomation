package com.bungdz.Wizards_App.ui.Devices;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bungdz.Wizards_App.MainActivity;
import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.adapter.DevicesAdapter;
import com.bungdz.Wizards_App.models.Device;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.networking.ThingsboardWebSocketManager;
import com.bungdz.Wizards_App.networking.WebSocketCallback;
import com.bungdz.Wizards_App.R;
import com.bungdz.Wizards_App.databinding.FragmentDevicesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DevicesFragment extends Fragment {
    private FragmentDevicesBinding binding;
    private DevicesAdapter adapter;
    private List<Device> devices_tb;
    private Handler handler = new Handler();
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private List<Pair<String, String>> keyValuePairList;
    private ThingsboardWebSocketManager webSocketManager;
    private String selectedElement, selectedDevice;
    private  String[] firstWs= {"0","0","0","0","0"};
    public DevicesFragment() {

    }
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

    private void showAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Thay mContext bằng context của bạn
        LayoutInflater inflater = LayoutInflater.from(getContext()); // Thay mContext bằng context của bạn
        View dialogView = inflater.inflate(R.layout.dialog_add_device, null);
        final Spinner spinnerElement = dialogView.findViewById(R.id.spinner_element);
        final Spinner spinnerDevice = dialogView.findViewById(R.id.spinner_device);
        // Tạo một mảng các mục cho Spinner
        String[] items = {"Node1", "Node2", "Node3", "Node4", "Node5", "Node6", "Node7", "Node8"};
        String[] itemsDevice = {"led1","led2", "led3", "led4", "temperature", "humidity", "light", "air"};
        // Tạo một ArrayAdapter để liên kết dữ liệu với Spinner
        ArrayAdapter<String> adapterElement = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, items);
        adapterElement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Đặt ArrayAdapter cho Spinner
        spinnerElement.setAdapter(adapterElement);
        spinnerElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedElement = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nếu không có mục nào được chọn
            }
        });
        // Tạo một ArrayAdapter để liên kết dữ liệu với Spinner
        ArrayAdapter<String> adapterDevice = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, itemsDevice);
        adapterDevice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Đặt ArrayAdapter cho Spinner
        spinnerDevice.setAdapter(adapterDevice);
        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedDevice = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nếu không có mục nào được chọn
            }
        });
        builder.setView(dialogView)
                .setTitle("Add Device")
                .setPositiveButton("Submit", (dialog, which) -> {
                    Device device = new Device(selectedElement);
                    if(selectedDevice.contains("air")){
                        selectedDevice = "aOnOff";
                    }
                    device.setDevice(selectedDevice);
                    device.setValue("0");
                    uiHandler.post(() -> adapter.addDevice(device));

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showSubsDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_subs_device, null);
        final Spinner spinnerElement = dialogView.findViewById(R.id.spinner_element);
        final Spinner spinnerDevice = dialogView.findViewById(R.id.spinner_device);
        // Tạo một mảng các mục cho Spinner
        String[] items = {"Node1", "Node2", "Node3", "Node4", "Node5", "Node6", "Node7", "Node8"};
        String[] itemsDevice = {"led1","led2", "led3", "led4", "temperature", "humidity", "light", "air"};
        // Tạo một ArrayAdapter để liên kết dữ liệu với Spinner
        ArrayAdapter<String> adapterElement = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, items);
        adapterElement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Đặt ArrayAdapter cho Spinner
        spinnerElement.setAdapter(adapterElement);
        spinnerElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedElement = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nếu không có mục nào được chọn
            }
        });
        // Tạo một ArrayAdapter để liên kết dữ liệu với Spinner
        ArrayAdapter<String> adapterDevice = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, itemsDevice);
        adapterDevice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Đặt ArrayAdapter cho Spinner
        spinnerDevice.setAdapter(adapterDevice);
        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedDevice = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nếu không có mục nào được chọn
            }
        });
        builder.setView(dialogView)
                .setTitle("Delete Device")
                .setPositiveButton("Submit", (dialog, which) -> {
                    keyValuePairList = adapter.getKeyValuePairList();
                    if(selectedDevice.contains("air")){
                        selectedDevice = "aOnOff";
                    }
                    String position = findValueByKey(selectedElement+selectedDevice);
                    if(position != null){
                        int index = ThingsBoardInfo.getIndexThingsBoard(selectedElement);
                        ThingsBoardHandle.httpDeleteTelematryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), selectedDevice, new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                uiHandler.post(() -> adapter.deleteDevice(Integer.parseInt(position)));
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        keyValuePairList = new ArrayList<>();
        devices_tb = new ArrayList<>();
//        try {
//            ThingsBoardHandle.mqttAndroidClientHiveMQ.subscribe(Constants.TOPIC_ACK, 1);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
        WebSocketCallback callback = new WebSocketCallback() {
            private String position;
            @Override
            public void onWebSocketConnected() {
                for(int index = 0; index < Constants.THINGS_BOARD_INFOS.length; index++){
                    webSocketManager.registerListen(Constants.THINGS_BOARD_INFOS[index].getDeviceID(),index);
                }
            }

            @Override
            public void onWebSocketMessage(String message) {
                getActivity().runOnUiThread(() -> {
                    String role = Device.getRole(ThingsboardWebSocketManager.getSubscriptionId(message));
                    int index = ThingsBoardInfo.getIndexThingsBoard(role);
                    if(firstWs[index].equals("0") &&  index!= 0){
                        firstWs[index] = "1";
                        List<Map.Entry<String, String>> keyValues = ThingsboardWebSocketManager.extractKeyValues(message);
                        for (Map.Entry<String, String> entry : keyValues) {
                            if(entry.getKey().contains("temperature") || entry.getKey().contains("humidity") || entry.getKey().contains("light")){
                                Device device = new Device(role);
                                device.setDevice(entry.getKey());
                                device.setValue(entry.getValue());
                                Log.d("Key", entry.getKey() + ": " + entry.getValue());
                                uiHandler.post(() -> adapter.addDevice(device));
                            }else if(entry.getKey().contains("led") || entry.getKey().contains("aOnOff")){
                                Device device = new Device(role);
                                device.setDevice(entry.getKey());
                                device.setValue(entry.getValue());
                                Log.d("Key", entry.getKey() + ": " + entry.getValue());
                                uiHandler.post(() -> adapter.addDevice(device));
                            }

                        }
                    }else if(firstWs[index].equals("1")){
                        keyValuePairList = adapter.getKeyValuePairList();
                        List<Map.Entry<String, String>> keyValues = ThingsboardWebSocketManager.extractKeyValues(message);
                        for (Map.Entry<String, String> entry : keyValues) {
                            if(entry.getKey().contains("temperature") || entry.getKey().contains("humidity") || entry.getKey().contains("light")){
                                keyValuePairList = adapter.getKeyValuePairList();
                                position = findValueByKey(role + entry.getKey());
                                Log.d("Bug", "Role: " + role + "Device: " + entry.getKey() + " position:" + position);
                                View itemView = binding.recyclerView.findViewWithTag(Integer.parseInt(Objects.requireNonNull(position)));
                                if (itemView != null) {
                                    TextView textViewData = itemView.findViewById(R.id.textViewItem_value);
                                    if(entry.getKey().contains("temperature")){
                                        textViewData.setText(entry.getValue() + "°C");
                                    }else if(entry.getKey().contains("humidity")){
                                        textViewData.setText(entry.getValue() + "%");
                                    }else{
                                        textViewData.setText(entry.getValue() + "lm");
                                    }
                                }
                            }else if(entry.getKey().contains("led") || entry.getKey().contains("aOnOff")){
                                keyValuePairList = adapter.getKeyValuePairList();
                                position = findValueByKey(role + entry.getKey());
                                Log.d("Bug", "Role: " + role + "Device: " + entry.getKey() + " position:" + position);
                                View itemView = binding.recyclerView.findViewWithTag(Integer.parseInt(Objects.requireNonNull(position)));
                                if (itemView != null) {
                                    ImageView imageViewItem = itemView.findViewById(R.id.imageViewItem);
                                    ToggleButton toggleButtonItem = itemView.findViewById(R.id.toggleButtonItem);
                                    if (entry.getValue().equals("1")) {
                                        if(entry.getKey().contains("led")){
                                            imageViewItem.setImageResource(R.drawable.lampbulbon);

                                        }else if(entry.getKey().contains("aOnOff")){
                                            imageViewItem.setImageResource(R.drawable.airconditioneron);

                                        }
                                        toggleButtonItem.setChecked(true);
                                    } else if (entry.getValue().equals("0")) {
                                        if(entry.getKey().contains("led")){
                                            imageViewItem.setImageResource(R.drawable.lampbulboff);

                                        }else if(entry.getKey().contains("aOnOff")){
                                            imageViewItem.setImageResource(R.drawable.airconditioneroff);

                                        }
                                        toggleButtonItem.setChecked(false);
                                    }
                                }
                            }

                        }
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

        ArrayList<String> listRole = ((MainActivity) requireActivity()).sharedViewModel.getListRole();
        listRole.add(0, "All");
//        for (String role : listRole) {
//            int index = ThingsBoardInfo.getIndexThingsBoard(role);
//            Log.d("role", role);
//            ThingsBoardHandle.getLastData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), new Callback() {
//                private String responseData;
//                @Override
//                public void onFailure(Request request, IOException e) {
//
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        responseData = response.body().string();
//                        //Log.d("responseData", responseData);
//                        response.body().close(); // Đảm bảo đóng response sau khi sử dụng nó
//                        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
//                        for (String key : jsonObject.keySet()) {
//                            if((key.contains("aOnOff") || key.contains("led") || key.contains("temperature") || key.contains("humidity") || key.contains("light"))){
//                                JsonArray jsonArray = jsonObject.getAsJsonArray(key);
//                                for (JsonElement jsonElement : jsonArray) {
//                                    Device device = new Device(role);
//                                    JsonObject subJsonObject = jsonElement.getAsJsonObject();
//                                    String value = subJsonObject.get("value").getAsString();
//                                    device.setDevice(key);
//                                    device.setValue(value);
//                                    Log.d("Key", key + ": " + value);
//                                    uiHandler.post(() -> adapter.addDevice(device));
//                                }
//                            }
//                        }
//                    }else {
//                        Log.d("ThingSpeak Receiver", "Not Success - code: " + response.code());
//                    }
//                }
//            });
//        }
        ArrayAdapter<String> elementAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, listRole);
        elementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        handler.postDelayed(() -> {
            try {
                webSocketManager = new ThingsboardWebSocketManager(ThingsBoardInfo.JWT_TOKEN, callback);
                webSocketManager.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        },1000);

        binding.spinnerSelectElement.setAdapter(elementAdapter);
        binding.spinnerSelectElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDeviceName  = parent.getItemAtPosition(position).toString();
                keyValuePairList = adapter.getKeyValuePairList();
                // Duyệt qua các position trong keyValuePairList và ẩn/hiển thị dựa trên tên thiết bị
                for (Pair<String, String> pair : keyValuePairList) {
                    int cardViewPosition = Integer.parseInt(pair.second);
                    if (pair.first.contains(selectedDeviceName)) {
                        // Hiển thị CardView
                        // Ẩn CardView
                        if (binding != null) {
                            View cardView = binding.recyclerView.findViewWithTag(cardViewPosition);
                            if (cardView != null) {
                                cardView.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("CardView", "khong tim thay tai: " + selectedDeviceName+ " "+ cardViewPosition);
                            }
                        }
                    } else {
                        // Ẩn CardView
                        if (binding != null) {
                            View cardView = binding.recyclerView.findViewWithTag(cardViewPosition);
                            if (cardView != null) {
                                cardView.setVisibility(View.GONE);
                            } else {
                                Log.d("CardView", "khong tim thay tai: " + selectedDeviceName+ " "+ cardViewPosition);
                            }
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có phần tử nào được chọn
            }
        });

        binding.imgBtnAdd.setOnClickListener(v -> showAddDeviceDialog());

        binding.imgBtnSubs.setOnClickListener(v -> showSubsDeviceDialog());

        adapter = new DevicesAdapter(getContext(),devices_tb);
        binding.recyclerView.setAdapter(adapter);
        return root;
    }
}
