package com.bbilandzi.diplomskiandroidapp.utils;

import java.util.Calendar;

public class DateTimeUtil {
    public static long getDateInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return calendar.getTimeInMillis();
    }
}
