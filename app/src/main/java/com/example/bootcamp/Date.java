package com.example.bootcamp;


import androidx.annotation.Nullable;
import java.util.Calendar;

public class Date {

    private static final int MIN_YEAR = 2021;
    private static final int MAX_YEAR = 2030;

    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        if (!validateDate(year, month, day)) {
            throw new IllegalArgumentException();
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    private boolean validateDate(int year, int month, int day) {
        if (!((year >= MIN_YEAR) && (year <= MAX_YEAR) && (month >= 1) && (month <= 12) && day >= 1 && day <= 31)) {
            return false;
        }

        if(month == 2) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            if ((cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365)) {
                return day <= 29;
            }
            return (day <= 28);
        }

        else if(month == 4 || month == 6 || month == 9 || month == 11) {
            return day <= 30;
        }

        return true;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        Date otherDate = (Date) obj;
        return (otherDate.getYear() == year && otherDate.getDay() == day && otherDate.getMonth() == month);
    }


}
