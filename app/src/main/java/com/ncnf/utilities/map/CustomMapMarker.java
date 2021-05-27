package com.ncnf.utilities.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A CustomMapMarker that holds the necessary information to display the info windows when clicked
 * and to lead to the linked pages. These Markers can be clustered together
 */
public class CustomMapMarker implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;
    private final List<Event> eventList;
    private final Organization org;
    private final boolean isEvent;

    /**
     * Creates a new CustomMapMarker which can be displayed on the map and clustered using a ClusterManager
     * @param lat The latitude of the anchor point in float
     * @param lon The longitude of the anchor point in float
     * @param title The title to show on the info window
     * @param snippet The snippet to show on the info window, this is the second text that appears below the title
     * @param eventList The list of events this marker represents, must have isEvent true
     * @param org The organization this marker represents, must have isEvent false
     * @param isEvent True if this organization represents an event, false otherwise
     */
    public CustomMapMarker(float lat, float lon, String title, String snippet, List<Event> eventList, Organization org, boolean isEvent){
        this(new LatLng(lat, lon), title, snippet, eventList, org, isEvent);
    }

    /**
     * Creates a new CustomMapMarker which can be displayed on the map and clustered.
     * @param position The anchor position of the marker on the map as a LatLng
     * @param title The title to show on the info window
     * @param snippet The snippet to show on the info window, this is the second text that appears below the title
     * @param eventList The list of events this marker represents, must have isEvent true
     * @param org The organization this marker represents, must have isEvent false
     * @param isEvent True if this organization represents an event, false otherwise
     */
    public CustomMapMarker(LatLng position, String title, String snippet, List<Event> eventList, Organization org, boolean isEvent){
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.isEvent = isEvent;

        if (isEvent){
            this.eventList = eventList;
            this.org = null;
        } else {
            this.eventList = new ArrayList<>();
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
     * Returns the list of events held by this marker. Empty list if isEvent is false
     * @return A list of events, empty is not isEvent
     */
    public List<Event> getEventList() {return Collections.unmodifiableList(eventList);}

    /**
     * Returns the organization held by this marker. Null if isEvent is true
     * @return An Organization object or null if isEvent
     */
    public Organization getOrg() {
        return org;
    }

    /**
     * Returns a boolean indicating if this marker holds events or an organization
     * @return True if this marker represents events, false if it represents an organization
     */
    public boolean isEvent(){
        return isEvent;
    }
}
