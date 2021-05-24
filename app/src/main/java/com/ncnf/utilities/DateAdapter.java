package com.ncnf.utilities;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateAdapter {

    private final int year;
    private final int month;
    private final int day;

    private final int hour;
    private final int minute;

    private final LocalDateTime date;

    /**
     * Constructor using LocalDateTime date as initial value
     */
    public DateAdapter(LocalDateTime date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
        this.date = date;

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    /**
     * Constructor using year, month and day to initialize its attributes
     */
    public DateAdapter(int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        this.year = year;
        this.month = month;
        this.day = day;

        date = LocalDateTime.of(year, Month.of(month), day, 0, 0);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    /**
     * Constructor using year, month, day, hour and minute to initialize its attributes
     */
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

        date = LocalDateTime.of(year, Month.of(month), day, hour, minute);

        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Getter for attributes
     */
    public LocalDateTime getDate() {
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

    /**
     * Parse a string to a date
     * @param s String we want to parse
     * @return Parsed date
     */
    public static LocalDateTime toDate(String s) {

        Date date;

        try {
            //TODO Fix this
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            date = sdf.parse(s);
        }
        catch (Exception e) {
            return null;
        }

        return LocalDateTime.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes());

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
