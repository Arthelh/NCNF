package com.ncnf.map;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class NCNFMarkerTest {

    @Test
    public void testMarkers(){
        LatLng position = new LatLng(46f, 32f);
        NCNFMarker marker = new NCNFMarker(46f, 32f, "TestTitle", "TestSnippet", new ArrayList<>(), NCNFMarker.TYPE.EVENT);

        assertEquals(position, marker.getPosition());
        assertEquals("TestTitle", marker.getTitle());
        assertEquals("TestSnippet", marker.getSnippet());
        assertEquals(new ArrayList<>(), marker.getEventList());
        assertEquals(NCNFMarker.TYPE.EVENT, marker.getType());
    }
}
