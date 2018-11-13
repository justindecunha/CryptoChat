package com.example.jesus.cryptochat.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * This class provides static methods to serialize/deserialize message objects to/from a byte array,
 * which can then be sent/received over the network.
 */
public class MessageSerializer {

    // Convert a message object into a byte-array
    public static byte[] serialize(final Message message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    // Convert a byte-array into a message object
    public static Message deserialize(final byte[] serializedMessage) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedMessage);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bis);
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
