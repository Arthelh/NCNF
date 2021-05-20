package com.ncnf.utilities.map;

import com.ncnf.utilities.map.Venue;
import com.ncnf.utilities.map.VenueProvider;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProvidersTest {

    @Test
    public void testVenueProvider(){
        VenueProvider ep = new VenueProvider();
        List<Venue> list = ep.getAll();

        assertEquals(8, list.size());

        assertTrue(list.contains(new Venue("EPFL", 46.5191f, 6.5668f)));
        assertTrue(list.contains(new Venue("Lausanne Opera", 46.5180f, 6.6369f)));
        assertTrue(list.contains(new Venue("Les Docks", 46.5224f, 6.6193f)));
        assertTrue(list.contains(new Venue("Espace Arsenic", 46.5227f, 6.6216f)));
        assertTrue(list.contains(new Venue("Théâtre Sévelin 36", 46.5225f, 6.6197f)));
        assertTrue(list.contains(new Venue("MAD", 46.5219f, 6.6272f)));
        assertTrue(list.contains(new Venue("UniL", 46.5211f, 6.5802f)));
        assertTrue(list.contains(new Venue("Renens - Salle de Concerts", 46.5338f, 6.5914f)));
    }

}
