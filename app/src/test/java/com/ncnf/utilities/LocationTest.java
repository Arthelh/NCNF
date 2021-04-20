package com.ncnf.utilities;
import org.junit.Test;
import com.ncnf.utilities.Location;

import static org.junit.Assert.*;

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
}
