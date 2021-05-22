package com.ncnf.utilities.settings;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;

public class Settings {

    //Distance in km
    public final static int MAX_ACCEPTED_DISTANCE = 100;
    private static int currentMaxDistance = 25;
    private static LatLng userPosition = new LatLng(46.526120f, 6.576330f);
    private static LocalDate minDate = LocalDate.now();
    private static LocalDate maxDate = LocalDate.now().plusYears(2); //Arbitrary value

    /**
     * Sets the current maximum distance in km at which events and organizers will be shown
     * @param currentMaxDistance The max distance in km
     */
    public static void setCurrentMaxDistance(int currentMaxDistance) {
        Settings.currentMaxDistance = currentMaxDistance;
    }

    /**
     * Returns the maximum distance at which events and organizers will be shown
     * @return The distance in km
     */
    public static int getCurrentMaxDistance() {
        return currentMaxDistance;
    }

    /**
     * Sets the minimum date from which events will be shown
     * @param minDate The minimum Date in java.util.Date
     */
    public static void setMinDate(LocalDate minDate){
        if (minDate.isBefore(LocalDate.now()))
            return; //Can't set minimum date earlier than today
        Settings.minDate = minDate;
        if (Settings.maxDate.isBefore(minDate))
            setMaxDate(minDate);
    }

    /**
     * Sets the maximum date until which events will be shown
     * @param maxDate The max Date in java.util.Date
     */
    public static void setMaxDate(LocalDate maxDate){
        if (minDate.isBefore(LocalDate.now()))
            return; //Can't set maximum date earlier than today
        Settings.maxDate = maxDate;
        if (getMinDate().isAfter(maxDate))
            setMinDate(maxDate);
    }

    /**
     * Returns the minimum date from which events will be shown
     * @return The minimum date
     */
    public static LocalDate getMinDate() {
        return LocalDate.from(minDate);
    }

    /**
     * Returns the maximum date until which events will be shown
     * @return The maximum date
     */
    public static LocalDate getMaxDate() {
        return LocalDate.from(maxDate);
    }

    /**
     * Sets the current user position to the given LatLng values
     * @param userPosition A LatLng object with the coordinates to set
     */
    public static void setUserPosition(LatLng userPosition) {
        Settings.userPosition = new LatLng(userPosition.latitude, userPosition.longitude);
    }

    /**
     * Returns the current position of the user, represented as a LatLng
     * @return A LatLng object with the coordinates of the user
     */
    public static LatLng getUserPosition(){
        return new LatLng(Settings.userPosition.latitude, Settings.userPosition.longitude);
    }

    /**
     * Returns a boolean telling if the given date is included in the current date range
     * @param date The date to decide for
     * @return True if minDate <= date <= maxDate, false otherwise
     */
    public static boolean dateInRange(ChronoLocalDate date){
        return (minDate.isBefore(date) || minDate.isEqual(date)) && (maxDate.isEqual(date) || maxDate.isAfter(date));
    }

}
