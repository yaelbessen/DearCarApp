package com.example.dearcar;

/**
 * Created by Yael on 6/22/13.
 */
public class NavigationMainScreenEntry implements Item {
    public String destination;

    public NavigationMainScreenEntry(String destination) {
        this.destination = destination;
    }
    @Override
    public boolean isSection() {
        return false;
    }
}
