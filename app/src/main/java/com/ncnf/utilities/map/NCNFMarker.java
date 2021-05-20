package com.ncnf.utilities.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;

import java.util.Collections;
import java.util.List;

public class NCNFMarker implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;
    private final List<Event> eventList;
    private final Organization org;
    private final boolean isEvent;

    public NCNFMarker(float lat, float lon, String title, String snippet, List<Event> eventList, Organization org, boolean isEvent){
        this(new LatLng(lat, lon), title, snippet, eventList, org, isEvent);
    }

    public NCNFMarker(LatLng position, String title, String snippet, List<Event> eventList, Organization org, boolean isEvent){
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.isEvent = isEvent;

        if (isEvent){
            this.eventList = eventList;
            this.org = null;
        } else {
            this.eventList = null;
            this.org = org;
        }
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

    /**
     * Returns the list of events this marker represents, null if this marker represents an organization
     * @return A List containing events or a null object
     */
    public List<Event> getEventList() {return Collections.unmodifiableList(eventList);}

    /**
     * Returns the organization this marker represents. If isEvent is true, this will return null.
     * @return The organization or null
     */
    public Organization getOrg() {
        return org;
    }

    /**
     * Returns a boolean telling whether this marker represents events
     * @return True if this marker is an event marker, false otherwise
     */
    public boolean isEvent(){
        return isEvent;
    }
}
