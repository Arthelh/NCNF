package com.ncnf;

import com.ncnf.organizer.PublicOrganizer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PublicOrganizerTests {

    private final PublicOrganizer po = new PublicOrganizer("publicOrganizerTest");

    @Test
    public void getPublicOrganizerNameWorks(){assertEquals(po.getName(), "publicOrganizerTest");}
}
