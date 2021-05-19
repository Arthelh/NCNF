package com.ncnf.models;


import com.ncnf.models.Tag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TagTest {

    @Test
    public void testTag(){
        String emoji = "test1", name = "test2";
        Tag tag = new Tag(emoji, name);
        assertEquals(emoji, tag.getEmoji());
        assertEquals(name, tag.getName());

        String new_emoji = "test3", new_name = "test4";
        tag.setEmoji(new_emoji);
        tag.setName(new_name);
        assertEquals(new_emoji, tag.getEmoji());
        assertEquals(new_name, tag.getName());
    }
}
