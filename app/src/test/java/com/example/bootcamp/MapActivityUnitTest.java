package com.example.bootcamp;

import com.example.bootcamp.map.MapActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapActivityUnitTest {

    MapActivity activity = new MapActivity();
    GoogleMap mMap = new GoogleMap();

    @Test
    public void test_markers(){
        ArrayList<Marker> events_marker = activity.getEvent_markers();
        ArrayList<Marker> venue_marker = activity.getVenue_markers();
        assertThat(events_marker.size(), is(4));
        assertThat(venue_marker.size(), is(7));

        for (Marker m : events_marker){
            assertThat(m.isVisible(), is(true));
        }
        for (Marker m : venue_marker){
            assertThat(m.isVisible(), is(false));
        }
    }

}
