package com.ncnf.utilities;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class GroupAttendeeMarker implements ClusterItem {

    /**
     * This class was largely done thanks to the youtube Tutorial by CodingWithMitch.
     * Source code : https://github.com/mitchtabian/Google-Maps-2018/blob/building-a-service-for-gps-updates-end/app/src/main/java/com/codingwithmitch/googlemaps2018/services/LocationService.java
     */

    private LatLng position;
    private String title;
    private String snippet;

    private final String user;

    private final Bitmap bitmap;

    public GroupAttendeeMarker(LatLng position, String title, String snippet, String user, Bitmap bitmap) {
        this.user = user;
        this.position = position;
        this.title = title;
        this.snippet = snippet;

        this.bitmap = bitmap;
    }

    public String getUserId() {
        return user;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
         this.title = title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setPosition(LatLng latLng) {
        this.position = latLng;
    }
}
