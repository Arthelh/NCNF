package com.ncnf.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LocationTest {

    @Test
    public void basicGettersSettersWork() {
        Location loc = new Location(1, 1, "North Pole");
        assertEquals(loc.getLatitude(), 1, 0);
        assertEquals(loc.getLongitude(), 1, 0);
        assertEquals(loc.getAddress(), "North Pole");

        loc.setLatitude(2);
        loc.setLongitude(2);
        loc.setAddress("Another Pole");
        assertEquals(loc.getLatitude(), 2, 0);
        assertEquals(loc.getLongitude(), 2, 0);
        assertEquals(loc.getAddress(), "Another Pole");
    }

    @Test
    public void equalsWorks(){
        Location loc1 = new Location(0, 0, "loc1");
        Location loc2 = new Location(0, 1, "loc2");
        assertNotEquals(loc1, loc2);

        loc2.setLongitude(0);
        assertEquals(loc2, loc1);
    }
}
