package com.bbilandzi.diplomskiandroidapp.utils;

import com.bbilandzi.diplomskiandroidapp.model.EventDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;

public class DateTimeUtil {
    public static long getDateInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return calendar.getTimeInMillis();
    }

    public static Comparator<EventDTO> eventDateComparator() {
        return (event1, event2) -> {
            LocalDateTime date1 = LocalDateTime.parse(event1.getDate(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime date2 = LocalDateTime.parse(event2.getDate(), DateTimeFormatter.ISO_DATE_TIME);
            return date2.compareTo(date1);
        };
    }
}
