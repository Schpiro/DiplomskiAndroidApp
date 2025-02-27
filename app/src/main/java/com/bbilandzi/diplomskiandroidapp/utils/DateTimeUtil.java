package com.bbilandzi.diplomskiandroidapp.utils;

import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

public class    DateTimeUtil {
    public static long getDateInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return calendar.getTimeInMillis();
    }

    public static String getIsoIntoYearMonthDate(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
        return dateTime.format(formatter);
    }

    public static String getIsoIntoMinutesHourDayMonthYear(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy", Locale.getDefault());
        return dateTime.format(formatter);
    }

    public static String getIsoIntoHours(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        return dateTime.format(formatter);
    }

    public static String systemTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime utcDateTime = LocalDateTime.parse(date, formatter);
        return utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toString();
    }

    public static Comparator<EventDTO> eventDateComparator() {
        return (event1, event2) -> {
            LocalDateTime date1 = LocalDateTime.parse(event1.getDate(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime date2 = LocalDateTime.parse(event2.getDate(), DateTimeFormatter.ISO_DATE_TIME);
            return date2.compareTo(date1);
        };
    }

    public static Comparator<CommentDTO> commentDateComparator() {
        return (comment1, comment2) -> {
            LocalDateTime date1 = LocalDateTime.parse(comment1.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime date2 = LocalDateTime.parse(comment2.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME);
            return date2.compareTo(date1);
        };
    }

    public static Comparator<CommentDTO> commentDateComparatorReverse() {
        return (comment1, comment2) -> {
            LocalDateTime date1 = LocalDateTime.parse(comment1.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime date2 = LocalDateTime.parse(comment2.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME);
            return date1.compareTo(date2);
        };
    }
}
