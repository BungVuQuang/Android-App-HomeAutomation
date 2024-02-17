package com.bungdz.Wizards_App.adapter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Alarm;
import com.bungdz.Wizards_App.models.Device;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlarmAdapter  extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{
    private List<Alarm> dataList;
    private MqttAndroidClient mqttAndroidClient;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private int isDeleted = 1;
    private List<Pair<String, String>> keyValuePairList;
    public AlarmAdapter(List<Alarm> dataList, MqttAndroidClient mqttAndroidClient) {
        this.dataList = dataList;
        this.mqttAndroidClient=mqttAndroidClient;
        keyValuePairList = new ArrayList<>();
    }

    public List<Pair<String, String>> getKeyValuePairList() {
        return keyValuePairList;
    }
    public void addAlarm(Alarm alarm) {
        dataList.add(alarm);
        notifyItemInserted(dataList.size() - 1);
    }

    public void deleteAlarm(int position) {
        for (Iterator<Pair<String, String>> iterator = keyValuePairList.iterator(); iterator.hasNext();) {
            Pair<String, String> pair = iterator.next();
            String firstValue = pair.second;
            if (firstValue.equals(position)) {
                iterator.remove();
                isDeleted = 0;
            }
        }
        if(isDeleted == 0){
            isDeleted = 1;
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_alarm_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmAdapter.ViewHolder holder, int position) {
        Alarm alarm = dataList.get(position);
        keyValuePairList.add(new Pair<>(alarm.getId(), ""+position));
        holder.textViewRole.setText(alarm.getRole());
        holder.textViewIdAlarm.setText("ID:   " + alarm.getId());
        holder.textViewDeviceName.setText("Device:   " + alarm.getDevice());
        holder.textViewState.setText("State:   " + alarm.getState());
        holder.textViewDayAlarm.setText("Day:   " + alarm.getDate());
        holder.textViewTime.setText("Time:   " + alarm.getTime());
        if(alarm.getState().contains("1")){
            holder.textViewState.setText("State:   ON");
        }else{
            holder.textViewState.setText("State:   OFF");
        }
        holder.buttonDeleteAlarm.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            Alarm alarm1 = dataList.get(adapterPosition);
            String idAlarm = alarm1.getId();
            String deivceAlarm = alarm1.getRole();
            String selectedDevice = alarm1.getDevice();
            int index = ThingsBoardInfo.getIndexThingsBoard(deivceAlarm);
            ThingsBoardHandle.deleteAlarm(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), idAlarm, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Log.d("deleteAlarm", "Xoa thanh cong " + adapterPosition);
                    uiHandler.post(() -> {
                        for (Iterator<Pair<String, String>> iterator = keyValuePairList.iterator(); iterator.hasNext();) {
                            Pair<String, String> pair = iterator.next();
                            String firstValue = pair.first;
                            if (firstValue.contains(idAlarm)) {
                                iterator.remove();
                                isDeleted = 0;
                            }
                        }
                        if(isDeleted == 0){
                            isDeleted = 1;
                            dataList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        }
                    });
                }
            });
//            String message = "Delete " + deivceAlarm + " " + idAlarm;
//            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
//            try {
//                mqttAndroidClient.publish(Constants.TOPIC_ALARM, mqttMessage);
//				mqttAndroidClient.setCallback(new MqttCallback() {
//                                            @Override
//                                            public void connectionLost(Throwable cause) {
//                                                System.out.println("Connection lost!");
//                                            }
//
//                                            @Override
//                                            public void messageArrived(String topic, MqttMessage message) {
//												ThingsBoardHandle.mqttAndroidClientHiveMQ.setCallback(null);
//                                                System.out.println("Message received: " + new String(message.getPayload()));
//                                                String payload = new String(message.getPayload());
//                                                if(payload.contains("ack")){
//
//                                            DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference("Alarm").child(deivceAlarm).child(idAlarm);
//                                            nodeRef.removeValue();
//                                            dataList.remove(adapterPosition);
//                                            notifyItemRemoved(adapterPosition);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void deliveryComplete(IMqttDeliveryToken token) {
//                                                System.out.println("Delivery complete.");
//                                            }
//                                        });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewRole;
        public TextView textViewIdAlarm;
        public TextView textViewDeviceName;
        public TextView textViewDayAlarm;

        public TextView textViewTime;

        public TextView textViewState;
        public Button buttonDeleteAlarm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            textViewIdAlarm = itemView.findViewById(R.id.textViewIdAlarm);
            textViewDeviceName = itemView.findViewById(R.id.textViewDeviceName);
            textViewDayAlarm = itemView.findViewById(R.id.textViewDayAlarm);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewState = itemView.findViewById(R.id.textViewStateDevice);
            buttonDeleteAlarm = itemView.findViewById(R.id.buttonDeleteAlarm);
        }
    }
}
