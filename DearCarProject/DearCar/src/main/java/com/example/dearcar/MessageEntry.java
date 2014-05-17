package com.example.dearcar;

/**
 * Created by Yael on 6/11/13.
 */
public class MessageEntry {
    public String caller;
    public int pic;
    public int num_of_unread_messages;
    public String sound;

    public MessageEntry(String caller, int pic, int num_of_unread_messages, String sound) {
        this.caller = caller;
        this.pic = pic;
        this.num_of_unread_messages = num_of_unread_messages;
        this.sound = sound;
    }
}
