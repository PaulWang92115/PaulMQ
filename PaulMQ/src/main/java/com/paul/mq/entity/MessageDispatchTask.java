package com.paul.mq.entity;

public class MessageDispatchTask {

	private String clusters;
	private String topic;
	private Message message;
	public String getClusters() {
		return clusters;
	}
	public void setClusters(String clusters) {
		this.clusters = clusters;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	
	
}
