package com.ncnf.settings;

public class LanguagePack {

    public enum Language {
        EN(0), FR(1);

        public final int index;

        private Language(int index){
            this.index = index;
        }
    };

    public static final String[] settings_discard_button = {"DISCARD", "ANNULER"};
    public static final String[] settings_accept_button = {"ACCEPT", "ACCEPTER"};
    public static final String[] settings_distance_slider = {"Max distance = ", "Distance maximale = "};



}
