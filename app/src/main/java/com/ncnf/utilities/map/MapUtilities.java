package com.ncnf.utilities.map;

import com.google.android.gms.maps.model.LatLng;
import com.ncnf.utilities.settings.Settings;

import java.util.Arrays;
import java.util.List;

public class MapUtilities {

    public static final double LAT_TO_KM = 110.574;
    public static final double LONG_TO_KM = 111.320; //MUST BE ADJUSTED BY cos(lat)!!
    public static final List<String> supported_countries = Arrays.asList("CH");

    /**
     * Check if a position is the range of a target position
     * @param target_position Position to check
     * @param current_position Target position
     * @return Boolean if in range or not
     */
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
