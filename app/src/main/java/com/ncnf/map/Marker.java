package com.ncnf.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.ncnf.event.Event;

import java.util.Collections;
import java.util.List;

public class Marker implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;
    private final List<Event> eventList;

    public Marker(float lat, float lon, String title, String snippet, List<Event> eventList){
        this(new LatLng(lat, lon), title, snippet, eventList);
    }

    public Marker(LatLng position, String title, String snippet, List<Event> eventList){
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.eventList = eventList;
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

    public List<Event> getEventList() {return Collections.unmodifiableList(eventList);}
}
