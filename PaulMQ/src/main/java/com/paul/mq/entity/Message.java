package com.paul.mq.entity;

import java.io.Serializable;
/*
 * 
 */
public class Message extends AbstractMessage implements Serializable{
	
    private String msgId;
    private String queue;
    private Exchange exchange;
    private WorkMode mode;
    private byte[] body;
    private long timeStamp;
    
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	
	public Exchange getExchange() {
		return exchange;
	}
	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
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
	public WorkMode getMode() {
		return mode;
	}
	public void setMode(WorkMode mode) {
		this.mode = mode;
	}
    
    
}
