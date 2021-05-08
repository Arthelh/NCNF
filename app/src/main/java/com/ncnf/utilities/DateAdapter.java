package com.ncnf.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAdapter {

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    private Date date;

    public DateAdapter(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        this.date = date;

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public DateAdapter(int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        this.year = year;
        this.month = month;
        this.day = day;

        date = new Date(calendar.getTimeInMillis());

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public DateAdapter(int year, int month, int day, int hour, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        this.year = year;
        this.month = month;
        this.day = day;

        date = new Date(calendar.getTimeInMillis());

        this.hour = hour;
        this.minute = minute;
    }

    public Date getDate() {
        return date;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public static Date toDate(String s) {

        Date date;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            date = sdf.parse(s);
        }
        catch (Exception e) {
            return null;
        }

        return new Date(date.getTime());

    }

    @Override
    public String toString() {
        String thisMinute = minute + "";
        if(minute < 10) {
            thisMinute = "0" + minute;
        }

        return day + "/" + month + "/" + year + " " + hour + ":" + thisMinute;
    }
}
