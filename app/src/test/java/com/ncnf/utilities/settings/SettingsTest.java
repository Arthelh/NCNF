package com.ncnf.utilities.settings;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SettingsTest {

    @Test
    public void settingsBaseTest(){
        assertThat(Settings.MAX_ACCEPTED_DISTANCE, is(100));
    }

    @Test
    public void testSetAndGetDistance(){
        int new_value = 50;
        Settings.setCurrentMaxDistance(new_value);
        assertThat(Settings.getCurrentMaxDistance(), is(new_value));
    }

    @Test
    public void testSetAndGetMinDate(){
        LocalDate testDate = LocalDate.now().plusDays(7);
        assertNotEquals("First", testDate, Settings.getMinDate());
        Settings.setMinDate(testDate);
        assertEquals("Second", testDate, Settings.getMinDate());
    }

    @Test
    public void testSetAndGetLocation(){
        LatLng testPosition = new LatLng(0, 0);
        assertNotEquals(Settings.getUserPosition(), testPosition);

        Settings.setUserPosition(testPosition);
        assertEquals(Settings.getUserPosition(), testPosition);
    }

    @Test
    public void testSetAndGetMaxDate(){
        LocalDate testDate = LocalDate.now().plusDays(1);
        assertNotEquals(testDate, Settings.getMaxDate());
        Settings.setMaxDate(testDate);
        assertEquals(testDate, Settings.getMaxDate());
    }

    @Test
    public void testDateInRange(){
        LocalDate date3 = LocalDate.of(2022, 5, 22);
        LocalDate date2 = LocalDate.of(2021, 10, 22);
        LocalDate date1 = LocalDate.of(2021, 5, 22);

        Settings.setMinDate(date1); Settings.setMaxDate(date3);
        assertTrue(Settings.dateInRange(date2));

        Settings.setMinDate(date2);
        assertFalse(Settings.dateInRange(date1));

        Settings.setMinDate(date1); Settings.setMaxDate(date2);
        assertFalse(Settings.dateInRange(date3));
    }
}
