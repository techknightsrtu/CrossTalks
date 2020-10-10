package com.techknightsrtu.crosstalks.activity.chat.models;

import java.util.List;

public class ChatChannel {
    private String channelId;
    private List<String> userIds;

    public ChatChannel(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
