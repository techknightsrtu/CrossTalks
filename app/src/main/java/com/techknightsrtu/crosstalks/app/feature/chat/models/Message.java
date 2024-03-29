package com.techknightsrtu.crosstalks.app.feature.chat.models;

public class Message {

    private String timestamp;
    private String sender;
    private String senderAvatarName;
    private String sendAvatarId;
    private String receiver;
    private String message;
    private String replyMessage;
    private MessageType type;
    private boolean isSeen;

    public Message(String timestamp, String sender,
                   String senderAvatarName, String sendAvatarId,
                   String receiver, String message, String replyMessage, MessageType type, boolean isSeen) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.senderAvatarName = senderAvatarName;
        this.sendAvatarId = sendAvatarId;
        this.receiver = receiver;
        this.message = message;
        this.replyMessage = replyMessage;
        this.type = type;
        this.isSeen = isSeen;
    }

    public Message() {

    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }


    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
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

    public String getSenderAvatarName() {
        return senderAvatarName;
    }

    public void setSenderAvatarName(String senderAvatarName) {
        this.senderAvatarName = senderAvatarName;
    }

    public String getSendAvatarId() {
        return sendAvatarId;
    }

    public void setSendAvatarId(String sendAvatarId) {
        this.sendAvatarId = sendAvatarId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timestamp='" + timestamp + '\'' +
                ", sender='" + sender + '\'' +
                ", senderAvatarName='" + senderAvatarName + '\'' +
                ", sendAvatarId='" + sendAvatarId + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                '}';
    }
}
