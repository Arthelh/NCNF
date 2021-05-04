package com.ncnf.settings;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Settings {

    //Distance in km
    public final static int MAX_ACCEPTED_DISTANCE = 100;
    private static int currentMaxDistance = 25;
    private static LocalDate minDate = LocalDate.now();
    private static LocalDate maxDate = LocalDate.now().plusYears(2);

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
        Settings.minDate = minDate;
    }

    /**
     * Sets the maximum date until which events will be shown
     * @param maxDate The max Date in java.util.Date
     */
    public static void setMaxDate(LocalDate maxDate){
        Settings.maxDate = maxDate;
    }

    /**
     * Returns the minimum date from which events will be shown
     * @return The minimum date
     */
    public static LocalDate getMinDate() {
        return minDate;
    }

    /**
     * Returns the maximum date until which events will be shown
     * @return The maximum date
     */
    public static LocalDate getMaxDate() {
        return maxDate;
    }
}
