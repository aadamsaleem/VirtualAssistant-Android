package com.virtualassistant.models;

/**
 * Created by aadam on 4/12/17.
 */

public class ChatMessage {
    private boolean isURL, isMine;
    private String content;

    public ChatMessage(String message, boolean mine, boolean isURL) {
        content = message;
        isMine = mine;
        this.isURL = isURL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isURL() {
        return isURL;
    }

    public void setIsURL(boolean isURL) {
        this.isURL = isURL;
    }
}
