package com.ncnf.utilities.map;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomMapMarkerTest {

    @Test
    public void testMarkers(){
        LatLng position = new LatLng(46f, 32f);
        CustomMapMarker marker = new CustomMapMarker(46f, 32f, "TestTitle", "TestSnippet", new ArrayList<>(), true);

        assertEquals(position, marker.getPosition());
        assertEquals("TestTitle", marker.getTitle());
        assertEquals("TestSnippet", marker.getSnippet());
        assertEquals(new ArrayList<>(), marker.getEventList());
        assertTrue(marker.isEvent());
    }
}
