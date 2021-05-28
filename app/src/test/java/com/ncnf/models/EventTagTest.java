package com.ncnf.models;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTagTest {

    @Test
    public void getNameWorks(){
        String emoji = "test1", name = "test2";
        EventTag eventTag = new EventTag(emoji, name);
        assertEquals(name, eventTag.getName());

        String new_emoji = "test3", new_name = "test4";
        eventTag.setEmoji(new_emoji);
        eventTag.setName(new_name);
        assertEquals(new_name, eventTag.getName());
    }

    @Test
    public void getEmojiWorks(){
        String emoji = "test1", name = "test2";
        EventTag eventTag = new EventTag(emoji, name);
        assertEquals(emoji, eventTag.getEmoji());

        String new_emoji = "test3", new_name = "test4";
        eventTag.setEmoji(new_emoji);
        eventTag.setName(new_name);
        assertEquals(new_emoji, eventTag.getEmoji());
    }
}