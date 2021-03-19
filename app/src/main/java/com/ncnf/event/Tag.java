package com.ncnf.event;

import androidx.annotation.Nullable;

public class Tag {

    private String emoji;
    private String name;

    public Tag(String emoji, String name){
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
        Tag otherTag = (Tag) obj;
        return otherTag.getName().toUpperCase().replaceAll("\\s+","").equals(name.toUpperCase().replaceAll("\\s+",""));
    }
}
