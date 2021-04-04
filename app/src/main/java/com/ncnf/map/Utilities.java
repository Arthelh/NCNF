package com.ncnf.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.ncnf.R;
import com.ncnf.settings.Settings;

public class Utilities {

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
        return (delta_distance < Settings.getCurrent_max_distance());
    }

    // This method comes from https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    public static BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_baseline_accessibility_new_24);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_accessibility_new_24);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        vectorDrawable.setTint(0xFF0000);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
