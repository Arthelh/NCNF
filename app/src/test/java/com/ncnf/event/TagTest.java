package com.ncnf.event;
import com.ncnf.organizer.PublicOrganizer;



import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class TagTest {

    @Test
    public void settersWork() {
        Tag tag = new Tag("\uD83C\uDFB8", "Rock Music");
        tag.setEmoji("\uD83D\uDC93");
        assertEquals(tag.getEmoji(), "\uD83D\uDC93");
        tag.setName("Heart");
        assertEquals(tag.getName(), "Heart");
    }

}
