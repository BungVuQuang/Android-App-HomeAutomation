package com.bungdz.Wizards_App;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.DataPoint;
import com.bungdz.Wizards_App.models.DayAverageValue;
import com.bungdz.Wizards_App.models.DayTotalTime;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.databinding.ActivityChartBinding;
import com.bungdz.Wizards_App.utils.ChartUtils;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {
    private ActivityChartBinding binding;
    private int typeOption = 0;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String dateFromPicker, dateToPicker, airSelectedDevice;

    private void showDateFromPicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            dateFromPicker = dateFormatter.format(selectedDate.getTime());
            binding.textViewResultdateFrom.setText(dateFromPicker);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showDateToPicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            dateToPicker = dateFormatter.format(selectedDate.getTime());
            binding.textViewResultdateTo.setText(dateToPicker);
        }, year, month, day);

        datePickerDialog.show();
    }

    private long calculateTimeDifferenceMillis(DataPoint startTime, DataPoint endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date startDate = sdf.parse(startTime.getDate() + " " + startTime.getTime());
            Date endDate = sdf.parse(endTime.getDate() + " " + endTime.getTime());
            return endDate.getTime() - startDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot() ;
        setContentView(view);
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChartActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa các activity ở trên MainActivity
            startActivity(intent);
            finish(); // Kết thúc ChartActivity
        });
        ThingsBoardInfo[] thingBoardInfo = Constants.THINGS_BOARD_INFOS;
        // Tạo một mảng các mục cho Spinner
        String[] items = {"Date", "Hour"};
        // Tạo một ArrayAdapter để liên kết dữ liệu với Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Đặt ArrayAdapter cho Spinner
        binding.spinnerSelectOption.setAdapter(adapter);
        binding.spinnerSelectOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedText = parentView.getItemAtPosition(position).toString();
                if(selectedText.equals("Date")){
                    Log.d("spinner", "1");
                    binding.layoutPickDateFrom.setVisibility(View.VISIBLE);
                    binding.layoutPickDateTo.setVisibility(View.VISIBLE);
                    typeOption = 1;
                } else if (selectedText.equals("Hour")) {
                    Log.d("spinner", "0");
                    binding.layoutPickDateFrom.setVisibility(View.VISIBLE);
                    binding.layoutPickDateTo.setVisibility(View.GONE);
                    typeOption = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nếu không có mục nào được chọn
            }
        });

        String[] items_role = {"Node1", "Node2", "Node3", "Node4", "Node5", "Node6", "Node7", "Node8"};
        String[] itemsDevice = {"led1","led2", "led3", "led4", "temperature", "humidity", "light", "air"};
        ArrayAdapter<String> elementAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.custom_spinner_item, items_role);
        elementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerElement.setAdapter(elementAdapter);
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.custom_spinner_item, itemsDevice);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDevice.setAdapter(deviceAdapter);


        binding.buttonPickTimeFrom.setOnClickListener(v -> showDateFromPicker());

        binding.buttonPickTimeTo.setOnClickListener(v -> showDateToPicker());

        binding.btnRender.setOnClickListener(v -> {
                    String selectedElement = binding.spinnerElement.getSelectedItem().toString();
                    String selectedDevice = binding.spinnerDevice.getSelectedItem().toString();
                    long endTs = 0;
                    long startTs = 0;
                    binding.progressBar.setVisibility(View.VISIBLE);
                    if ((selectedDevice.equals("temperature") || selectedDevice.equals("humidity") || selectedDevice.equals("light")) && typeOption == 1) {
                        binding.tvChartTitle.setVisibility(View.VISIBLE);
                        binding.tvChartTitle.setText("Dữ liệu trung bình theo ngày của " + selectedDevice + " " + selectedElement);
                    } else if ((selectedDevice.equals("led") || selectedDevice.equals("fan") || selectedDevice.equals("air")) && typeOption == 1) {
                        binding.tvChartTitle.setVisibility(View.VISIBLE);
                        binding.tvChartTitle.setText("Dữ liệu tổng thời gian bật " + selectedDevice + " " + selectedElement);
                    } else if (typeOption == 1) {
                        binding.tvChartTitle.setVisibility(View.VISIBLE);
                        binding.tvChartTitle.setText("Dữ liệu tổng thời gian bật " + selectedDevice + " " + selectedElement);
                    } else if (typeOption == 0) {
                        binding.tvChartTitle.setVisibility(View.VISIBLE);
                        binding.tvChartTitle.setText("Dữ liệu trong ngày của " + selectedDevice + " " + selectedElement);
                    }
                    if (typeOption == 0) {
                        startTs = ChartUtils.convertDateTimeToTimestamp(dateFromPicker, "00:01");
                        endTs = ChartUtils.convertDateTimeToTimestamp(dateFromPicker, "23:59");
                    } else if (typeOption == 1) {
                        startTs = ChartUtils.convertDateTimeToTimestamp(dateFromPicker, "00:01");
                        endTs = ChartUtils.convertDateTimeToTimestamp(dateToPicker, "00:01");
                    }

                    int index = ThingsBoardInfo.getIndexThingsBoard(selectedElement);
                    airSelectedDevice = selectedDevice;
                    if(selectedDevice.contains("air")){
                        airSelectedDevice="aOnOff";
                    }
                    ThingsBoardHandle.getFieldData(ThingsBoardInfo.JWT_TOKEN,thingBoardInfo[index].getDeviceID(), airSelectedDevice, startTs, endTs, new Callback() {
                        private String responseData;

                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (response.isSuccessful()) {
                                responseData = response.body().string();
                                response.body().close(); // Đảm bảo đóng response sau khi sử dụng nó
                                binding.chartLayout.clear();
                                Log.d("ThingsBoard Receiver ", responseData);
                                List<DataPoint> dataPoints = DataPoint.parseDataFromResponseTB(airSelectedDevice,responseData);
                                List<DayTotalTime> totalTimeList = new ArrayList<>();
                                List<Entry> entries = new ArrayList<>();
                                List<DayTotalTime> uniqueTotalTimeList = new ArrayList<>();
                                List<DayAverageValue> averageValueList = new ArrayList<>();
                                if (typeOption == 1) {
                                    if (airSelectedDevice.contains("led") || airSelectedDevice.contains("fan") || airSelectedDevice.contains("air")) {
                                        DayTotalTime currentDayTotalTime = null;
                                        for (int i = 1; i < dataPoints.size(); i++) {
                                            DataPoint prevData = dataPoints.get(i - 1);
                                            DataPoint currentData = dataPoints.get(i);

                                            if (prevData.getValue() == 1.0 && currentData.getValue() == 0.0) {
                                                long timeDiffMillis = calculateTimeDifferenceMillis(prevData, currentData);

                                                if (currentDayTotalTime == null || !currentDayTotalTime.getDate().equals(currentData.getDate())) {
                                                    currentDayTotalTime = new DayTotalTime(currentData.getDate());
                                                    totalTimeList.add(currentDayTotalTime);
                                                }

                                                currentDayTotalTime.addTime(timeDiffMillis);
                                            }
                                        }
                                        for (DayTotalTime dayTotalTime : totalTimeList) {
                                            boolean isDateExist = false;

                                            for (DayTotalTime uniqueDayTotalTime : uniqueTotalTimeList) {
                                                if (uniqueDayTotalTime.getDate().equals(dayTotalTime.getDate())) {
                                                    isDateExist = true;
                                                    break;
                                                }
                                            }

                                            if (!isDateExist) {
                                                uniqueTotalTimeList.add(dayTotalTime);
                                            }
                                        }
                                        Log.d("CHART", "uniqueTotalTimeList.size: " + uniqueTotalTimeList.size());
                                        for (int i = 0; i < uniqueTotalTimeList.size(); i++) {
                                            long totalTimeMillis = uniqueTotalTimeList.get(i).getTotalTimeMillis();
                                            float totalTimeHours = totalTimeMillis / 3600000f; // Chuyển đổi từ ms sang giờ
                                            entries.add(new Entry(i, totalTimeHours));
                                        }
                                    } else {
                                        DayAverageValue currentDayAverageValue = null;
                                        for (DataPoint dataPoint : dataPoints) {
                                            float value = Float.parseFloat(Double.toString(dataPoint.getValue()));

                                            if (currentDayAverageValue == null || !currentDayAverageValue.getDate().equals(dataPoint.getDate())) {
                                                currentDayAverageValue = new DayAverageValue(dataPoint.getDate(), value);
                                                averageValueList.add(currentDayAverageValue);
                                            } else {
                                                currentDayAverageValue.addValue(value);
                                            }
                                        }
                                        Log.d("CHART", "currentDayAverageValue.size: " + averageValueList.size() + "currentDayAverageValue.size: " + averageValueList.get(0).getDate());
                                        for (int i = 0; i < averageValueList.size(); i++) {
                                            entries.add(new Entry(i, Float.parseFloat(Double.toString(averageValueList.get(i).getAverageValue()))));
                                        }
                                    }
                                } else {
                                    Log.d("CHART", "typeOption:" + typeOption + "sizeFilter: " + dataPoints.size());
                                    for (int i = 0; i < dataPoints.size(); i++) {
                                        entries.add(new Entry(i, Float.parseFloat(Double.toString(dataPoints.get(i).getValue()))));
                                    }
                                }
                                LineDataSet dataSet;
                                if (airSelectedDevice.contains("temperature")) {
                                    dataSet = new LineDataSet(entries, "°C");
                                } else if (airSelectedDevice.contains("humidity")) {
                                    dataSet = new LineDataSet(entries, "%");
                                }else if (airSelectedDevice.contains("light")) {
                                    dataSet = new LineDataSet(entries, "lux");
                                }  else if (typeOption == 0) {
                                    dataSet = new LineDataSet(entries, "Level");
                                } else {
                                    dataSet = new LineDataSet(entries, "Hour");
                                }
                                dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                                dataSet.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                                List<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(dataSet);
                                dataSet.setValueTextSize(14f); // Đặt cỡ chữ cho giá trị dữ liệu
                                LineData lineData = new LineData(dataSets);
                                binding.chartLayout.setData(lineData);
                                List<String> xAxisLabels = new ArrayList<>();
                                if (typeOption == 1) { // Nếu đang chọn tính năng vẽ theo ngày
                                    if (airSelectedDevice.contains("led") || airSelectedDevice.contains("fan") || airSelectedDevice.contains("air")) {
                                        for (DayTotalTime dataPoint : uniqueTotalTimeList) {
                                            xAxisLabels.add(dataPoint.getDate());
                                        }
                                    } else if (airSelectedDevice.contains("temperature") || airSelectedDevice.contains("humidity") || airSelectedDevice.contains("light")) {
                                        for (DayAverageValue dataPoint : averageValueList) {
                                            xAxisLabels.add(dataPoint.getDate());
                                        }
                                    }

                                } else { // Nếu đang chọn tính năng vẽ theo giờ trong ngày
                                    for (DataPoint dataPoint : dataPoints) {
                                        Log.d("CHART", "xAxisLabels Time:" + dataPoint.getTime());
                                        xAxisLabels.add(dataPoint.getTime());
                                    }
                                }
                                XAxis xAxis = binding.chartLayout.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels)); // Replace with your XAxisValueFormatter
                                xAxis.setTextSize(6f); // Đặt cỡ chữ cho trục XAxis
                                YAxis yAxisLeft = binding.chartLayout.getAxisLeft();
                                YAxis yAxisRight = binding.chartLayout.getAxisRight();
                                yAxisLeft.setDrawTopYLabelEntry(true); // Cho phép hiển thị tiêu đề ở trên đồ thị
                                yAxisRight.setDrawTopYLabelEntry(true);
                                binding.chartLayout.setDragEnabled(true);
                                binding.chartLayout.setScaleEnabled(true);
                                binding.chartLayout.setPinchZoom(true);
                                binding.chartLayout.getXAxis().setAxisMinimum(0f); // Đặt giá trị tối thiểu cho trục X
                                binding.chartLayout.getXAxis().setAxisMaximum(dataPoints.size()+1); // Đặt giá trị tối đa cho trục X

                                binding.chartLayout.setVisibleXRangeMaximum(8); // Đặt số lượng dữ liệu hiển thị trên trục X
                                binding.chartLayout.setVisibleXRangeMinimum(1); // Đặt số lượng dữ liệu tối thiểu hiển thị trên trục X
                                binding.chartLayout.moveViewToX(dataPoints.size() - 8); // Đưa biểu đồ tới vị trí cuối cùng
                                binding.chartLayout.getDescription().setEnabled(false); // Bật tiêu đề
                                binding.chartLayout.invalidate();
                                runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));
                            } else {
                                Log.d("ThingSpeak Receiver", "Not Success - code: " + response.code());
                            }
                        }

                    });
                });
        }
}
