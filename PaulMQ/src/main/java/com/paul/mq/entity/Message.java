package com.paul.mq.entity;

import java.io.Serializable;
/*
 * 
 */
public class Message extends AbstractMessage implements Serializable{
	
    private String msgId;
    private String topic;
    private byte[] body;
    private long timeStamp;
    
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
    
    
}
