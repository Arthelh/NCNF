package com.ncnf.models;

import androidx.annotation.Nullable;

public class EventTag {

    private String emoji;
    private String name;

    public EventTag(String emoji, String name){
        this.emoji = emoji;
        this.name = name;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getName() {
        return name;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        EventTag otherEventTag = (EventTag) obj;
        return otherEventTag.getName().toLowerCase().trim().equals(name.toLowerCase().trim());
    }
}
