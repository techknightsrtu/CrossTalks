package com.techknightsrtu.crosstalks.app.feature.chat.models;

import java.util.List;

public class ChatChannel {

    private String channelId;

    public ChatChannel(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

}
