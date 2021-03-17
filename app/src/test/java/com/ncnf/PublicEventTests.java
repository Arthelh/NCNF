package com.ncnf;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ncnf.event.EventType;
import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

import static org.junit.Assert.*;

public class PublicEventTests {

    private PublicOrganizer po = new PublicOrganizer("publicOrganizer");
    private PublicEvent pe = new PublicEvent(po, EventType.Museum, "museumEvent", "EventData", LocalDateTime.now());


    @Test
    public void getOrganizerNameWorks(){assertEquals(po.getName(), "publicOrganizer");}

    @Test
    public void getOrganizerWorks(){assertEquals(pe.getOrganizer(), po);}

    @Test
    public void getNameWorks(){assertEquals(pe.getName(), "museumEvent");}

    // useless
    @Test
    public void getUUIDWorks(){assertEquals(pe.getUID().length(), UUID.randomUUID().toString().length());}

    @Test
    public void getTypeWorks(){assertEquals(pe.getType(), EventType.Museum);}

    @Test
    public void getDataWorks(){assertEquals(pe.getData(), "EventData");}

    @Test
    public void setDataWorks(){
        pe.setData("TestSetData");
        assertEquals(pe.getData(), "TestSetData");
    }

}
