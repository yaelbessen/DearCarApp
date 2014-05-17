package com.example.dearcar;

public class ItemSection implements Item {

    public final String title;
    public final String color;

    public ItemSection(String title, String color) {
        this.title = title;
        this.color = color;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}
