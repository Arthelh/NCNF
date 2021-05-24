package com.ncnf.models;

import androidx.annotation.Nullable;

public class Tag {

    private String emoji;
    private String name;

    /**
     * Public constructor
     * @param emoji Emoji of the Tag
     * @param name Name of the tag
     */
    public Tag(String emoji, String name){
        this.emoji = emoji;
        this.name = name;
    }

    /**
     * Getters for the attributes
     */
    public String getEmoji() {
        return emoji;
    }
    public String getName() {
        return name;
    }

    /**
     * Setters for the attributes
     */
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Tag otherTag = (Tag) obj;
        return otherTag.getName().toLowerCase().trim().equals(name.toLowerCase().trim());
    }
}
