package com.bungdz.Wizards_App.utils;

import com.bungdz.Wizards_App.models.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChartUtils {
    public ChartUtils(){

    }

    public static long convertDateTimeToTimestamp(String dateStr, String timeStr) {
        try {
            String dateTimeStr = dateStr + " " + timeStr + ":01";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Thiết lập múi giờ GMT+07:00
            TimeZone timeZone = TimeZone.getTimeZone("GMT+07:00");
            dateFormat.setTimeZone(timeZone);

            Date date = dateFormat.parse(dateTimeStr);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<DataPoint> filterDataByDateRange(List<DataPoint> dataPoints, String fromDate, String toDate) {
        List<DataPoint> filteredData = new ArrayList<>();

        for (DataPoint dataPoint : dataPoints) {
            if (isDateInRange(dataPoint.getDate(), fromDate, toDate)) {
                filteredData.add(dataPoint);
            }
        }

        return filteredData;
    }

    public static List<DataPoint> filterDataBySelectedDate(List<DataPoint> dataPoints, String selectedDate) {
        List<DataPoint> filteredData = new ArrayList<>();

        for (DataPoint dataPoint : dataPoints) {
            if (isDateSelected(dataPoint.getDate(), selectedDate)) {
                filteredData.add(dataPoint);
            }
        }

        return filteredData;
    }

    // Hàm kiểm tra ngày có nằm trong khoảng không
    public static boolean isDateInRange(String date, String fromDate, String toDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = sdf.parse(date);
            Date fromDateParsed = sdf.parse(fromDate);
            Date toDateParsed = sdf.parse(toDate);

            return currentDate.after(fromDateParsed) && currentDate.before(toDateParsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm kiểm tra ngày có phải là ngày được chọn không
    public static boolean isDateSelected(String date, String selectedDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = sdf.parse(date);
            Date selectedDateParsed = sdf.parse(selectedDate);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(currentDate);
            cal2.setTime(selectedDateParsed);

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}
