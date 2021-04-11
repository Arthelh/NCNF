package com.ncnf.map;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MapHandlerTest {

    @Test
    public void testPositionSetterGetter(){
        MapHandler mapHandler = new MapHandler(null, null, null, null);

        LatLng testPosition = new LatLng(0, 0);
        assertNotEquals(mapHandler.getUserPosition(), testPosition);

        mapHandler.setUserPosition(testPosition);
        assertEquals(mapHandler.getUserPosition(), testPosition);
    }
}
