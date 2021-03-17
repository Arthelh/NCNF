package com.ncnf;

import org.junit.Test;


import java.util.Date;
import java.util.UUID;

import com.ncnf.event.EventType;
import com.ncnf.event.Location;

import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

import static org.junit.Assert.*;

public class PublicEventTests {

    private PublicOrganizer po = new PublicOrganizer("publicOrganizer");
    private PublicEvent pe = new PublicEvent("museumEvent", new Date(), new Location(0, 0, "test"), "EventData", EventType.Museum, 0, 0, po);

    @Test
    public void getOrganizerNameWorks(){assertEquals(po.getName(), "publicOrganizer");}

    @Test
    public void getOrganizerWorks(){assertEquals(pe.getOrganizer(), po);}

    @Test
    public void getNameWorks(){assertEquals(pe.getName(), "museumEvent");}

    // useless
    @Test
    public void getUUIDWorks(){assertEquals(pe.getUuid().toString().length(), UUID.randomUUID().toString().length());}


    @Test
    public void getTypeWorks(){assertEquals(pe.getType(), EventType.Museum);}

    @Test

    public void getDataWorks(){assertEquals(pe.getDescription(), "EventData");}

    @Test
    public void setDataWorks(){
        pe.setDescription("TestSetData");
        assertEquals(pe.getDescription(), "TestSetData");

    }

}
