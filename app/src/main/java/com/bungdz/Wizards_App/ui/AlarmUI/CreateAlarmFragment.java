package com.bungdz.Wizards_App.ui.AlarmUI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bungdz.Wizards_App.MainActivity;
import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Alarm;
import com.bungdz.Wizards_App.models.SharedViewModel;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.R;
import com.bungdz.Wizards_App.databinding.FragmentCreateAlarmBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class CreateAlarmFragment extends Fragment {
    private FragmentCreateAlarmBinding binding;
    private SimpleDateFormat dateFormatter; // Định dạng ngày tháng
    private SimpleDateFormat timeFormatter; // Định dạng giờ phút
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private DatabaseReference elementReference;
    private DatabaseReference deviceReference;
    private DatabaseReference alarmReference;
    private SharedViewModel sharedViewModel;
    private static final String TAG = "CreateAlarmFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreateAlarmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        alarmReference = FirebaseDatabase.getInstance().getReference("Alarm");
        ArrayList<String> listRole = ((MainActivity) requireActivity()).sharedViewModel.getListRole();
        Log.d("listRole", listRole.toString());
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        binding.buttonSetAlarm.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            String selectedElement = binding.spinnerElement.getSelectedItem().toString();
            String selectedDevice = binding.spinnerDevice.getSelectedItem().toString();
            String selectedState = binding.spinnerState.getSelectedItem().toString();
            String selectedDate = binding.textViewResultdate.getText().toString().replace("Date: ", "");
            String selectedTime = binding.textViewResulttime.getText().toString().replace("Time: ", "");

            String selectedDateTimeStr = selectedDate + " " + selectedTime;

            Calendar selectedDateTime = Calendar.getInstance();
            try {

                selectedDateTime.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(selectedDateTimeStr));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            Calendar currentTime = Calendar.getInstance();
            if (selectedDateTime.after(currentTime)) {
                int indexRole = ThingsBoardInfo.getIndexThingsBoard(selectedElement);
                ThingsBoardHandle.getAllKeysAlarm(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[indexRole].getDeviceID(), new Callback() {
                    private String responseData;
                    private String state;
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        responseData = response.body().string();
                        response.body().close();
                        String dataWithoutBrackets = responseData.substring(1, responseData.length() - 1);

                        String[] dataArray = dataWithoutBrackets.split(",");
                        int maxNumber = 0;

                        for (String item : dataArray) {
                            String cleanItem = item.trim().replace("\"", "");
                            String[] parts = cleanItem.split("_");
                            if (parts.length == 2) {
                                try {
                                    int number = Integer.parseInt(parts[1]);
                                    if (number > maxNumber) {
                                        maxNumber = number;
                                    }
                                } catch (NumberFormatException e) {

                                }
                            }
                        }
                        maxNumber++;
                        String orderedID = selectedElement + "_" + maxNumber;
                        if(selectedState.contains("OFF")){
                            state = "0";
                        }else{
                            state = "1";
                        }
                        Alarm alarm = new Alarm(selectedElement,orderedID,selectedDevice,selectedDate,selectedTime,state);
                        ThingsBoardHandle.setAlarm(alarm, ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[indexRole].getDeviceID(), new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                uiHandler.post(() -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                });
                            }
                            @Override
                            public void onResponse(Response response) throws IOException {
                                Log.d("Alarm", "Gui Thanh Cong");
                                uiHandler.post(() -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                });

                            }
                        });
                    }
                });
//                DatabaseReference elementReference = alarmReference.child(selectedElement);
//                elementReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        long currentMaxOrderItem = 0;
//                        for (DataSnapshot alarmSnapshot : dataSnapshot.getChildren()) {
//                            String alarmID = alarmSnapshot.getKey();
//                            long orderItem = Long.parseLong(alarmID.split("_")[1]);
//                            currentMaxOrderItem = Math.max(currentMaxOrderItem, orderItem);
//                        }
//                        currentMaxOrderItem++;
//                        String orderedID = selectedElement + "_" + currentMaxOrderItem;
//                        // Tạo một đối tượng Alarm
//                        Alarm alarm = new Alarm();
//                        alarm.setRole(selectedElement);
//                        alarm.setDevice(selectedDevice);
//                        alarm.setDate(selectedDate);
//                        alarm.setTime(selectedTime);
//
//                        alarm.setId(orderedID);
//
//                        String message = "Alarm " + selectedElement + " " + orderedID + " " + selectedDevice + " " + selectedDate + " " + selectedTime; // Thay đổi nội dung publish tại đây
//                        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
//                        try {
//                            ThingsBoardHandle.mqttAndroidClientHiveMQ.publish(Constants.TOPIC_ALARM, mqttMessage);
//										ThingsBoardHandle.mqttAndroidClientHiveMQ.setCallback(new MqttCallback() {
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
//                        // Gửi dữ liệu lên Firebase
//                        DatabaseReference alarmReference = FirebaseDatabase.getInstance().getReference("Alarm")
//                                .child(selectedElement)
//                                .child(orderedID);
//                        alarmReference.setValue(alarm);
//                        binding.progressBar.setVisibility(View.GONE);
//                        Log.d("Alarm", "Gui Thanh Cong");
//                        Toast.makeText(getContext(), "Hẹn giờ thành công!!", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void deliveryComplete(IMqttDeliveryToken token) {
//                                                System.out.println("Delivery complete.");
//                                            }
//                                        });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }else {
                uiHandler.post(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi rồi? Bạn định quay ngược thời gian à?", Toast.LENGTH_SHORT).show();
                });
            }
        });

        binding.buttonPickDate.setOnClickListener(v -> showDatePicker());

        binding.buttonPickTime.setOnClickListener(v -> showTimePicker());

        ArrayAdapter<String> elementAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, listRole);
        elementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerElement.setAdapter(elementAdapter);

        String[] itemsDevice = {"led1","led2", "led3", "led4", "temperature", "humidity", "light", "air"};
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, itemsDevice);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDevice.setAdapter(deviceAdapter);

        String[] state = {"OFF", "ON"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, state);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerState.setAdapter(stateAdapter);

//        // Khởi tạo DatabaseReference cho các node cần truy cập trong Firebase
//        elementReference = FirebaseDatabase.getInstance().getReference("Element");
//        deviceReference = FirebaseDatabase.getInstance().getReference("Element");
//        // Lấy dữ liệu từ Firebase và đưa vào spinner_element
//        elementReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<String> elementList = new ArrayList<>();
//                for (DataSnapshot elementSnapshot : dataSnapshot.getChildren()) {
//                    String element = elementSnapshot.getKey();
//                    elementList.add(element);
//                }
//
//                ArrayAdapter<String> elementAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, elementList);
//                elementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerElement.setAdapter(elementAdapter);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });

        // Xử lý sự kiện khi người dùng chọn một phần tử trong spinner_element
//        binding.spinnerElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedElement = parent.getItemAtPosition(position).toString();
//                // Lấy dữ liệu từ Firebase và đưa vào spinner_device dựa trên phần tử đã chọn trong spinner_element
//                deviceReference.child(selectedElement).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        List<String> deviceList = new ArrayList<>();
//                        for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
//                            String device = deviceSnapshot.getKey();
//                            deviceList.add(device);
//                        }
//
//                        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, deviceList);
//                        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        binding.spinnerDevice.setAdapter(deviceAdapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        return root;
    }
    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            String formattedDate = dateFormatter.format(selectedDate.getTime());
            binding.textViewResultdate.setText("Date: " + formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedTime.set(Calendar.MINUTE, minute1);
            String formattedTime = timeFormatter.format(selectedTime.getTime());
            binding.textViewResulttime.setText("Time: " + formattedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }
}
