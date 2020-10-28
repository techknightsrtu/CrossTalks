package com.techknightsrtu.crosstalks.activity.chat.models;

public class EngagedChatChannel {
    private String channelId;
    private String lastActive;
    private String containsChats;

    public EngagedChatChannel() {
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public String getContainsChats() {
        return containsChats;
    }

    public void setContainsChats(String containsChats) {
        this.containsChats = containsChats;
    }
}
