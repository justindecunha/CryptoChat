package com.example.jesus.cryptochat.messaging;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.jesus.cryptochat.encryption.SimpleCrypt;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class that represents a message to be sent/received
 */
public class Message implements Serializable, Parcelable {

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(final Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(final int size) {
            return new Message[size];
        }
    };
    private final int msgId;
    private final String displayName;
    private String message;

    public Message(final int msgId, final String displayName, final String message) {
        this.msgId = msgId;
        this.displayName = displayName;
        this.message = message;
    }

    private Message(final Parcel in) {
        msgId = in.readInt();
        displayName = in.readString();
        message = in.readString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }


    private int getMsgId() {
        return msgId;
    }

    // in-built method to decrypt the message using a specified password
    public void decrypt(final String password) throws Exception {
        message = SimpleCrypt.decrypt(message, password);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, final int flags) {
        dest.writeInt(msgId);
        dest.writeString(displayName);
        dest.writeString(message);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (!(o instanceof Message)) {
            return false;
        }

        Message oAsMsg = (Message) o;

        return oAsMsg.getMsgId() == this.getMsgId() && oAsMsg.getMessage().equals(this.getMessage()) && oAsMsg.getDisplayName().equals(this.getDisplayName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgId, displayName, message);
    }
}
