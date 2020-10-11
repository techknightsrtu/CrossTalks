package com.techknightsrtu.crosstalks.activity.chat.models;

public class Message {

    private String timestamp;
    private String sender;
    private String senderAvatarName;
    private String sendAvatarId;
    private String receiver;
    private String message;
    private MessageType type;

    public Message(String timestamp, String sender,
                   String senderAvatarName, String sendAvatarId,
                   String receiver, String message, MessageType type) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.senderAvatarName = senderAvatarName;
        this.sendAvatarId = sendAvatarId;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
