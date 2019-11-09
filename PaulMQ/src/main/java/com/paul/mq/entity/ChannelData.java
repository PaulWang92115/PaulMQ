package com.paul.mq.entity;

import io.netty.channel.Channel;

public class ChannelData {
	private Channel channel;
	private String clientId;
	
    public ChannelData(Channel channel, String clientId) {
        this.channel = channel;
        this.clientId = clientId;
    }

	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
