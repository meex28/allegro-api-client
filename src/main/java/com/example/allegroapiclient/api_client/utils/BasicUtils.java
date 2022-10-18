package com.example.allegroapiclient.api_client.utils;

import java.util.Calendar;
import java.util.Date;

public class BasicUtils {
    public static Date addHoursToDate(Date date, int hours){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static Date getCurrentTimeSubHours(int hours){
        return addHoursToDate(new Date(), -hours);
    }
}
