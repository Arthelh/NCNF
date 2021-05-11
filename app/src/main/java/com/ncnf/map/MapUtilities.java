package com.ncnf.map;

import com.google.android.gms.maps.model.LatLng;
import com.ncnf.settings.Settings;

public class MapUtilities {

    public static final double LAT_TO_KM = 110.574;
    public static final double LONG_TO_KM = 111.320; //MUST BE ADJUSTED BY cos(lat)!!
    public static final String[] supported_countries = {"ch"};

    public static boolean position_in_range(LatLng target_position, LatLng current_position){
        //1 lat deg = 110.574km
        //1 long deg = 111.320 * cos(lat)
        double delta_lat_km = Math.abs(LAT_TO_KM * (target_position.latitude - current_position.latitude));
        double delta_long_km = Math.abs(LONG_TO_KM *
                (target_position.longitude * Math.cos(Math.toRadians(target_position.latitude))
                        - current_position.longitude * Math.cos(Math.toRadians(current_position.latitude))));
        double delta_distance = Math.sqrt(delta_lat_km * delta_lat_km + delta_long_km * delta_long_km);
        return (delta_distance < Settings.getCurrentMaxDistance());
    }

}
