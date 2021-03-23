package com.ncnf.settings;

import com.ncnf.settings.LanguagePack;
import com.ncnf.settings.Settings;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void test_set_and_get_lang(){
        assertThat(Settings.getLang(), is(LanguagePack.Language.EN));
        LanguagePack.Language new_lang = LanguagePack.Language.FR;
        Settings.setLang(new_lang);
        assertThat(Settings.getLang(), is(LanguagePack.Language.FR));
    }

    @Test
    public void test_languages(){
        assertThat(LanguagePack.Language.EN.index, is(0));
        assertThat(LanguagePack.Language.FR.index, is(1));
    }
}
