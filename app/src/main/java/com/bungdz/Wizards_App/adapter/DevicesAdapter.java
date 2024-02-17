package com.bungdz.Wizards_App.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Device;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.R;
import com.bungdz.Wizards_App.databinding.CardviewDeviceControlBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private CardviewDeviceControlBinding binding;
    private List<Device> devices;
    private List<Pair<String, String>> keyValuePairList; // Danh sách key-value cua Cardview
    private DatabaseReference threshReference;
    private Context mContext; // Add this member variable

    public DevicesAdapter(Context context,List<Device> devices) {
        this.mContext = context;
        this.devices = devices;
        keyValuePairList = new ArrayList<>();
    }
    private List<Device> devicesList = new ArrayList<>(); // Khởi tạo danh sách

    public void addDevice(Device device) {
        devicesList.add(device); // Thêm device vào danh sách
        notifyItemInserted(devicesList.size() - 1); // Thông báo vị trí mục đã thêm
    }
    public void deleteDevice(int position) {
        devicesList.remove(position);
        notifyItemRemoved(position);
    }
    private String valueToggleButton;
    private Handler handler = new Handler();
    private Handler handlerUI = new Handler(Looper.getMainLooper());
    private static final String TAG = "MQTTExample";

    public List<Pair<String, String>> getKeyValuePairList() {
        return keyValuePairList;
    }

    public void removeCallbacksAndMessages() {
        handler.removeCallbacksAndMessages(null);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardviewDeviceControlBinding binding;

        public ViewHolder(CardviewDeviceControlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CardviewDeviceControlBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        Device device = devicesList.get(position);
        if(device.getDevice().contains("led")){
                        Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice() + " "+ position);
                        keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
                        holder.binding.textViewItemValue.setVisibility(View.GONE);
                        holder.binding.ButtonSetting.setVisibility(View.GONE);
                        holder.binding.editTextItem.setVisibility(View.GONE);
                        holder.binding.linearLayoutThresh.setVisibility(View.GONE);
                        holder.binding.imageViewItem.setVisibility(View.VISIBLE);
                        holder.binding.toggleButtonItem.setVisibility(View.VISIBLE);
                        //holder.binding.textViewItemRole.setText(device.getDevice().substring(0, 1).toUpperCase() + device.getDevice().substring(1));
                        holder.binding.textViewItemRole.setText(device.getDeviceName().substring(0, 1).toUpperCase() + device.getDeviceName().substring(1));
                        holder.binding.textViewItemDevice.setText(device.getDevice().substring(0, 1).toUpperCase() + device.getDevice().substring(1));
                        holder.binding.imageViewItemName.setImageResource(R.drawable.idea);
                        holder.binding.textViewItemDevice.setVisibility(View.VISIBLE);
                        if(device.getValue().equals("0")){
                            holder.binding.toggleButtonItem.setChecked(false);
                            holder.binding.imageViewItem.setImageResource(R.drawable.lampbulboff);
                        }else{
                            holder.binding.toggleButtonItem.setChecked(true);
                            holder.binding.imageViewItem.setImageResource(R.drawable.lampbulbon);
                        }
                        holder.binding.toggleButtonItem.setTag(device.getDevice());
                        holder.binding.toggleButtonItem.setOnClickListener(v -> {
                            // Check network connectivity before proceeding
//                            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                                @Override
//                                public void onAvailable(Network network) {
                                    // Internet connection is available, proceed with the action
                                String valueToggleButton;
                                int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                                if (holder.binding.toggleButtonItem.isChecked()) {
                                    holder.binding.imageViewItem.setImageResource(R.drawable.lampbulbon);
                                    valueToggleButton = "1";
                                } else {
                                    holder.binding.imageViewItem.setImageResource(R.drawable.lampbulboff);
                                    valueToggleButton = "0";
                                }
                                    ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), device.getDevice(), valueToggleButton, new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Response response) {
//                                            handlerUI.post(() -> {
//
//                                                Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                            });
                                        }
                                    });
//                                }
//
//                                @Override
//                                public void onLost(Network network) {
//                                    // Internet connection is lost, handle accordingly
//                                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            };
//                            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                        });
                    }
        else if(device.getDevice().contains("aOnOff")){
            Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice());
            keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
            holder.binding.textViewItemValue.setVisibility(View.GONE);
            holder.binding.ButtonSetting.setVisibility(View.GONE);
            holder.binding.editTextItem.setVisibility(View.GONE);
            holder.binding.linearLayoutThresh.setVisibility(View.GONE);
            holder.binding.imageViewItem.setVisibility(View.VISIBLE);
            holder.binding.toggleButtonItem.setVisibility(View.VISIBLE);
            holder.binding.textViewItemRole.setText(device.getDeviceName().substring(0, 1).toUpperCase() + device.getDeviceName().substring(1));
            holder.binding.textViewItemDevice.setVisibility(View.GONE);
            holder.binding.imageViewItemName.setImageResource(R.drawable.air_conditioner);
            if(device.getValue().equals("0")){
                holder.binding.toggleButtonItem.setChecked(false);
                holder.binding.imageViewItem.setImageResource(R.drawable.airconditioneroff);
            }else{
                holder.binding.toggleButtonItem.setChecked(true);
                holder.binding.imageViewItem.setImageResource(R.drawable.airconditioneron);
            }
            holder.binding.toggleButtonItem.setTag(device.getDevice());
            holder.binding.toggleButtonItem.setOnClickListener(v -> {
                // Check network connectivity before proceeding
//                            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                                @Override
//                                public void onAvailable(Network network) {
                // Internet connection is available, proceed with the action
                String valueToggleButton;
                int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                if (holder.binding.toggleButtonItem.isChecked()) {
                    holder.binding.imageViewItem.setImageResource(R.drawable.airconditioneron);
                    valueToggleButton = "1";
                } else {
                    holder.binding.imageViewItem.setImageResource(R.drawable.airconditioneroff);
                    valueToggleButton = "0";
                }
                ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), device.getDevice(), valueToggleButton, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) {
//                                            handlerUI.post(() -> {
//
//                                                Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                            });
                    }
                });
//                                }
//
//                                @Override
//                                public void onLost(Network network) {
//                                    // Internet connection is lost, handle accordingly
//                                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            };
//                            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            });
        }
        else if (device.getDevice().contains("humidity")){
                        Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice());
                        keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
                        holder.binding.imageViewItem.setVisibility(View.GONE);
                        holder.binding.toggleButtonItem.setVisibility(View.GONE);
                        holder.binding.editTextItem.setVisibility(View.VISIBLE);
                        holder.binding.linearLayoutThresh.setVisibility(View.VISIBLE);
                        holder.binding.ButtonSetting.setVisibility(View.VISIBLE);
                        holder.binding.textViewItemValue.setVisibility(View.VISIBLE);
                        holder.binding.textViewItemRole.setText(device.getDeviceName().substring(0, 1).toUpperCase() + device.getDeviceName().substring(1));
                        holder.binding.imageViewThresh.setImageResource(R.drawable.tthumidity);
                        holder.binding.imageViewItemName.setImageResource(R.drawable.humidity);
                        holder.binding.textViewItemValue.setText(device.getValue() + "%");
                        holder.binding.textViewItemDevice.setVisibility(View.GONE);
                        holder.binding.imageBtnSync.setOnClickListener(v -> {
                            int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                            ThingsBoardHandle.getLastDataByKey(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), "hPeriod", new Callback() {
                                private String responseData;
                                private int value;
                                @Override
                                public void onFailure(Request request, IOException e) {

                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    responseData = response.body().string();
                                    response.body().close();
                                    JsonElement jsonElement = JsonParser.parseString(responseData);
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    JsonArray hPeriodArray = jsonObject.getAsJsonArray("hPeriod");
                                    JsonObject hPeriodObject = hPeriodArray.get(0).getAsJsonObject();
                                    if(!hPeriodObject.get("value").toString().contains("null")){
                                        value = hPeriodObject.get("value").getAsInt();
                                    }
                                    handlerUI.post(() -> {
                                        holder.binding.textViewItemThresh.setText(value + "p/lần");
                                    });
                                }
                            });
                        });
                        holder.binding.ButtonSetting.setOnClickListener(v -> {
                            // Check network connectivity before proceeding
//                            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                                @Override
//                                public void onAvailable(Network network) {
                                    // Internet connection is available, proceed with the action
                                    String inputValue = holder.binding.editTextItem.getText().toString().trim();
                                    int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                                    if (inputValue.isEmpty()) {
                                        Toast.makeText(v.getContext(), "Please enter a value!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), "hPeriod", inputValue, new Callback() {
                                            @Override
                                            public void onFailure(Request request, IOException e) {

                                            }

                                            @Override
                                            public void onResponse(Response response) {

//                                                handlerUI.post(() -> {
//                                                    Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                                });
                                            }
                                        });
                                    }
//                                }
//                                @Override
//                                public void onLost(Network network) {
//                                    // Internet connection is lost, handle accordingly
//                                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            };
//                            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                        });
                    }
        else if (device.getDevice().contains("temperature")){
                        Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice());
                        keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
                        holder.binding.imageViewItem.setVisibility(View.GONE);
                        holder.binding.toggleButtonItem.setVisibility(View.GONE);
                        holder.binding.editTextItem.setVisibility(View.VISIBLE);
                        holder.binding.linearLayoutThresh.setVisibility(View.VISIBLE);
                        holder.binding.ButtonSetting.setVisibility(View.VISIBLE);
                        holder.binding.textViewItemValue.setVisibility(View.VISIBLE);
            holder.binding.textViewItemRole.setText(device.getDeviceName().substring(0, 1).toUpperCase() + device.getDeviceName().substring(1));
                        holder.binding.imageViewThresh.setImageResource(R.drawable.hightemperature);
            holder.binding.textViewItemDevice.setVisibility(View.GONE);
                        holder.binding.imageViewItemName.setImageResource(R.drawable.temperature);
                        holder.binding.textViewItemValue.setText(device.getValue() + "°C");

                        holder.binding.imageBtnSync.setOnClickListener(v -> {
                            int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                            ThingsBoardHandle.getLastDataByKey(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), "tPeriod", new Callback() {
                                private String responseData;
                                private int value;
                                @Override
                                public void onFailure(Request request, IOException e) {

                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    responseData = response.body().string();
                                    response.body().close();
                                    JsonElement jsonElement = JsonParser.parseString(responseData);
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    JsonArray tPeriodArray = jsonObject.getAsJsonArray("tPeriod");
                                    JsonObject tPeriodObject = tPeriodArray.get(0).getAsJsonObject();
                                    if(!tPeriodObject.get("value").toString().contains("null")){
                                         value = tPeriodObject.get("value").getAsInt();
                                    }
                                    handlerUI.post(() -> {
                                        holder.binding.textViewItemThresh.setText(value + "p/lần");
                                    });
                                }
                            });
                        });

                        holder.binding.ButtonSetting.setOnClickListener(v -> {
                            // Check network connectivity before proceeding
//                            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                                @Override
//                                public void onAvailable(Network network) {
                                    // Internet connection is available, proceed with the action
                            String inputValue = holder.binding.editTextItem.getText().toString().trim();
                                    int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                            if (inputValue.isEmpty()) {
                                // Hiển thị cảnh báo khi EditText không có giá trị
                                Toast.makeText(v.getContext(), "Please enter a value!", Toast.LENGTH_SHORT).show();
                            } else {
                                ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), "tPeriod", inputValue, new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Response response) {

//                                        handlerUI.post(() -> {
//                                            Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                        });
                                    }
                                });
                        }
//                    }
//                                @Override
//                                public void onLost(Network network) {
//                                    // Internet connection is lost, handle accordingly
//                                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            };
//                            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                        });
                    }
        else if (device.getDevice().contains("light")){
            Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice());
            keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
            holder.binding.imageViewItem.setVisibility(View.GONE);
            holder.binding.toggleButtonItem.setVisibility(View.GONE);
            holder.binding.editTextItem.setVisibility(View.VISIBLE);
            holder.binding.linearLayoutThresh.setVisibility(View.VISIBLE);
            holder.binding.ButtonSetting.setVisibility(View.VISIBLE);
            holder.binding.textViewItemValue.setVisibility(View.VISIBLE);
            holder.binding.textViewItemRole.setText(device.getDeviceName().substring(0, 1).toUpperCase() + device.getDeviceName().substring(1));
            holder.binding.imageViewThresh.setImageResource(R.drawable.spotlight);
            holder.binding.imageViewItemName.setImageResource(R.drawable.sun);
            holder.binding.textViewItemDevice.setVisibility(View.GONE);
            holder.binding.textViewItemValue.setText(device.getValue() + "lm");

            holder.binding.imageBtnSync.setOnClickListener(v -> {
                int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                ThingsBoardHandle.getLastDataByKey(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), "lPeriod", new Callback() {
                    private String responseData;
                    private int value;
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        responseData = response.body().string();
                        response.body().close();
                        JsonElement jsonElement = JsonParser.parseString(responseData);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonArray lPeriodArray = jsonObject.getAsJsonArray("lPeriod");
                        JsonObject lPeriodObject =lPeriodArray.get(0).getAsJsonObject();
                        if(!lPeriodObject.get("value").toString().contains("null")){
                            value = lPeriodObject.get("value").getAsInt();
                        }
                        handlerUI.post(() -> {
                            holder.binding.textViewItemThresh.setText(value + "p/lần");
                        });
                    }
                });
            });

            holder.binding.ButtonSetting.setOnClickListener(v -> {
                // Check network connectivity before proceeding
//                ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                    @Override
//                    public void onAvailable(Network network) {
                        // Internet connection is available, proceed with the action
                        String inputValue = holder.binding.editTextItem.getText().toString().trim();
                        int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                        if (inputValue.isEmpty()) {
                            // Hiển thị cảnh báo khi EditText không có giá trị
                            Toast.makeText(v.getContext(), "Please enter a value!", Toast.LENGTH_SHORT).show();
                        } else {
                            ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), "lPeriod", inputValue, new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {

                                }

                                @Override
                                public void onResponse(Response response) {

//                                    handlerUI.post(() -> {
//                                        Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                    });
                                }
                            });
                        }
//                    }
//                    @Override
//                    public void onLost(Network network) {
//                        // Internet connection is lost, handle accordingly
//                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                    }
//                };
//                connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            });
        }
        else if (device.getDevice().contains("fan")){
                        Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice());
                        keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
                        holder.binding.textViewItemRole.setText(device.getDeviceName());
                        holder.binding.textViewItemValue.setVisibility(View.GONE);
                        holder.binding.textViewItemValue.setText("");
                        holder.binding.linearLayoutThresh.setVisibility(View.GONE);
                        holder.binding.ButtonSetting.setVisibility(View.GONE);
                        holder.binding.editTextItem.setVisibility(View.GONE);
                        holder.binding.imageViewItemName.setImageResource(R.drawable.fanname);
                        holder.binding.imageViewItem.setVisibility(View.VISIBLE);
                        holder.binding.toggleButtonItem.setVisibility(View.VISIBLE);
                        holder.binding.imageViewItem.setImageResource(R.drawable.fan);
                        if (device.getValue().equals("0")) {
                            holder.binding.toggleButtonItem.setChecked(false);

                            holder.binding.imageViewItem.clearAnimation();
                        } else {
                            holder.binding.toggleButtonItem.setChecked(true);
                            Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.rotationfan);
                            holder.binding.imageViewItem.startAnimation(animation);
                        }
                        holder.binding.toggleButtonItem.setTag(device.getDevice());
                        holder.binding.toggleButtonItem.setOnClickListener(v -> {
                            // Check network connectivity before proceeding
//                            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                                @Override
//                                public void onAvailable(Network network) {
                            String valueToggleButton;
                                    int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                            if (holder.binding.toggleButtonItem.isChecked()) {
                                Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.rotationfan);
                                holder.binding.imageViewItem.startAnimation(animation);
                                valueToggleButton = "1";
                            } else {
                                holder.binding.imageViewItem.clearAnimation();
                                valueToggleButton = "0";
                            }
                            ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), device.getDevice(), valueToggleButton, new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Response response) {
//
//                                            handlerUI.post(() -> {
//                                                Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                            });
                                        }
                                    });
//                            handler.postDelayed(() -> {
//                                    // Show a toast for ack timeout
//                                    Toast.makeText(mContext, "Lost connection to the gateway!", Toast.LENGTH_SHORT).show();
//                                    if (holder.binding.toggleButtonItem.isChecked()) {
//                                        holder.binding.toggleButtonItem.setChecked(false);
//                                        holder.binding.imageViewItem.clearAnimation();
//                                    } else {
//                                        holder.binding.toggleButtonItem.setChecked(true);
//                                        Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.rotationfan);
//                                        holder.binding.imageViewItem.startAnimation(animation);
//                                    }
//                }, 4000); // Delayed time in milliseconds (3 seconds)
//                    }
//
//                                @Override
//                                public void onLost(Network network) {
//                                    // Internet connection is lost, handle accordingly
//                                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            };
//                            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                        });
                    }
        if(device.getDevice().contains("device")){
                        Log.d(TAG, "Role: " + device.getDeviceName() + "Device: " + device.getDevice());
                        keyValuePairList.add(new Pair<>(device.getDeviceName()+device.getDevice(), ""+position));
                        holder.binding.textViewItemValue.setVisibility(View.GONE);
                        holder.binding.ButtonSetting.setVisibility(View.GONE);
                        holder.binding.editTextItem.setVisibility(View.GONE);
                        holder.binding.linearLayoutThresh.setVisibility(View.GONE);
                        holder.binding.imageViewItem.setVisibility(View.VISIBLE);
                        holder.binding.toggleButtonItem.setVisibility(View.VISIBLE);
                        holder.binding.textViewItemRole.setText(device.getDeviceName());
                        if(device.getDevice().equals("device1")){
                            holder.binding.imageViewItemName.setImageResource(R.drawable.one);
                        }else if(device.getDevice().equals("device2")){
                            holder.binding.imageViewItemName.setImageResource(R.drawable.two);
                        }else if(device.getDevice().equals("device3")){
                            holder.binding.imageViewItemName.setImageResource(R.drawable.three);
                        }else if(device.getDevice().equals("device4")){
                            holder.binding.imageViewItemName.setImageResource(R.drawable.four);
                        }
                        if(device.getValue().equals("0")){
                            holder.binding.toggleButtonItem.setChecked(false);
                            holder.binding.imageViewItem.setImageResource(R.drawable.device_off);
                        }else{
                            holder.binding.toggleButtonItem.setChecked(true);
                            holder.binding.imageViewItem.setImageResource(R.drawable.device_on);
                        }
                        holder.binding.toggleButtonItem.setTag(device.getDevice());
                        holder.binding.toggleButtonItem.setOnClickListener(v -> {
                            // Check network connectivity before proceeding
//                            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
//                            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//                                @Override
//                                public void onAvailable(Network network) {
                                    // Internet connection is available, proceed with the action
                                    int index = ThingsBoardInfo.getIndexThingsBoard(device.getDeviceName());
                                    handler.post(() -> {
                                        if (holder.binding.toggleButtonItem.isChecked()) {
                                            holder.binding.imageViewItem.setImageResource(R.drawable.device_on);
                                            valueToggleButton = "1";
                                        } else {
                                            holder.binding.imageViewItem.setImageResource(R.drawable.device_off);
                                            valueToggleButton = "0";
                                        }
                                    });
                                    ThingsBoardHandle.httpPostTelemetryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), device.getDevice(), valueToggleButton, new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Response response) {
//                                            handlerUI.post(() -> {
//                                                Toast.makeText(mContext, "Đã gửi thành công!!", Toast.LENGTH_SHORT).show();
//                                            });
                                        }
                                    });
//                                }
//                                @Override
//                                public void onLost(Network network) {
//                                    // Internet connection is lost, handle accordingly
//                                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            };
//                            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                        });
                    }
    }

    @Override
    public int getItemCount() {
        return devicesList.size();

    }
}
