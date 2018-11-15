# CryptoChat

CryptoChat is a serverless LAN chat app with support for encryption. It was designed to showcase decentralized [multicast group communication](https://en.wikipedia.org/wiki/Multicast), in which each individual client broadcasts messages to the group as a whole, rather than relying on client-server communication.

## Getting Started

### Requirements

- Android version 4.4 (KITKAT) or greater
- Access to a Wi-Fi network

### Installation

CryptoChat is available for installation through the Google Play Store.

More advanced users can also compile and install the apk from the sources provided.

## Demo

[![](http://img.youtube.com/vi/8kR8F2K20U0/0.jpg)](http://www.youtube.com/watch?v=8kR8F2K20U0 "CryptoChat")

## Features

### LAN Communication

The app's ideal use case is within a university/college campus. A large number of individuals connected to the local network can communicate with each other in an anonymous group chat.

### AES Encryption

Users can optionally enable encryption inside the settings screen. With encryption enabled, all communication sent/received will be encrypted/decrypted with the user specified password. Users with matching passwords will be able to understand each others communication, while everyone else will observe their communication as unreadable ciphertext.

### Channel Switching

Should a group chat become crowded with people, users can switch channels by specifying a different port to communicate on in the advanced settings. Users can also change the IP address used, however please ensure the IP used is a [valid IPv4 multicast address](https://en.wikipedia.org/wiki/Multicast_address#IPv4).

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
