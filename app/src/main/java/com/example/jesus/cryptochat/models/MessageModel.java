package com.example.jesus.cryptochat.models;

/**
 * Contains the basic components of a message.
 */
public class MessageModel {
    private String displayName;
    private String message;

    public MessageModel(final String displayName, final String message) {
        this.displayName = displayName;
        this.message = message;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }
}
