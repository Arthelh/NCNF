package com.ncnf;

import com.ncnf.settings.Settings;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SettingsTest {

    @Test
    public void test_set_and_get(){
        assertThat(Settings.getCurrent_max_distance(), is(25));
        int new_value = 50;
        Settings.setCurrent_max_distance(new_value);
        assertThat(Settings.getCurrent_max_distance(), is(new_value));
    }
}
