package com.example.jesus.cryptochat;

/**
 * This interface contains constants used globally throughout the application
 */
interface Constants {

    // The multicast port number
    int PORT = 40556;

    // The size of the packets
    int PACKET_SIZE = 512;

    // The multicast ip address
    String NETWORK_IP = "224.168.5.200";

    // The character set used for messages
    String MESSAGE_CHARSET = "UTF-8";

    // Max message size, going over this risks losing packets because UDP
    int MSG_MAX_BYTES = 450;

    // Time to live value for packets
    int TTL = 64;

    // Algorithm name
    String ALGORITHM = "AES";

    // Required password length for AES
    int PWD_LENGTH = 16;

    // The token we use to separate displayName from message
    String PARSE_TOKEN = "!@!";
}
