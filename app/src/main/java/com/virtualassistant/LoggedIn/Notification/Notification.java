package com.virtualassistant.LoggedIn.Notification;

/**
 * Created by aadam on 12/4/2017.
 */

public class Notification {
    public final String id;
    public final String content;
    public final String details;

    public Notification(String id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }
}