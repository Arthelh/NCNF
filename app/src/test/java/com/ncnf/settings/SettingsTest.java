package com.ncnf.settings;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

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
        Settings.setCurrent_max_distance(new_value);
        assertThat(Settings.getCurrent_max_distance(), is(new_value));
    }

    @Test
    public void testSetAndGetLocation(){
        LatLng testPosition = new LatLng(0, 0);
        assertNotEquals(Settings.getUserPosition(), testPosition);

        Settings.setUserPosition(testPosition);
        assertEquals(Settings.getUserPosition(), testPosition);
    }

}
