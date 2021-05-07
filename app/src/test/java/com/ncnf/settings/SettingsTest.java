package com.ncnf.settings;

import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SettingsTest {

    @Test
    public void settings_base_test(){
        assertThat(Settings.MAX_ACCEPTED_DISTANCE, is(100));
    }

    @Test
    public void test_set_and_get_distance(){
        //assertThat(Settings.getCurrent_max_distance(), is(25));
        int new_value = 50;
        Settings.setCurrentMaxDistance(new_value);
        assertThat(Settings.getCurrentMaxDistance(), is(new_value));
    }

    @Test
    public void testSetAndGetMinDate(){
        LocalDate testDate = LocalDate.now().plusDays(1);
        assertNotEquals(testDate, Settings.getMinDate());
        Settings.setMinDate(testDate);
        assertEquals(testDate, Settings.getMinDate());
    }

    @Test
    public void testSetAndGetMaxDate(){
        LocalDate testDate = LocalDate.now().plusDays(1);
        assertNotEquals(testDate, Settings.getMaxDate());
        Settings.setMaxDate(testDate);
        assertEquals(testDate, Settings.getMaxDate());
    }
}
