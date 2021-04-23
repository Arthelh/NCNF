package com.ncnf.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Marker implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;

    public Marker(float lat, float lon, String title, String snippet){
        this(new LatLng(lat, lon), title, snippet);
    }

    public Marker(LatLng position, String title, String snippet){
        this.position = position;
        this.title = title;
        this.snippet = snippet;
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

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }
}
