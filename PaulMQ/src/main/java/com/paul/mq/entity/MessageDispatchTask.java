package com.paul.mq.entity;

public class MessageDispatchTask {

	private String consumerId;
	private Message message;

	
	public String getConsumerId() {
		return consumerId;
	}
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	
	
}
