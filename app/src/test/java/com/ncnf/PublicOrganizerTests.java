package com.ncnf;

import org.junit.Test;

import com.ncnf.organizer.PublicOrganizer;

import static org.junit.Assert.*;

public class PublicOrganizerTests {

    private PublicOrganizer po = new PublicOrganizer("publicOrganizerTest");

    @Test
    public void getPublicOrganizerNameWorks(){assertEquals(po.getName(), "publicOrganizerTest");}
}
