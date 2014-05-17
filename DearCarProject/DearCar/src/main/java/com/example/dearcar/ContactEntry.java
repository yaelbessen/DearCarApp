package com.example.dearcar;

/**
 * Created by Yael on 6/20/13.
 */
public class ContactEntry implements Item {

    public int pic_left;
    public String name_left;
    public String number_left;
    public int pic_right;
    public String name_right;
    public String number_right;

    public ContactEntry(int pic_left, String name_left, String number_left,
                        int pic_right, String name_right, String number_right) {
        this.pic_left = pic_left;
        this.name_left = name_left;
        this.number_left = number_left;
        this.pic_right = pic_right;
        this.name_right = name_right;
        this.number_right = number_right;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
