package com.paul.mq.entity;

import java.io.Serializable;

public class UnRegisterMessage extends AbstractMessage implements Serializable{
	
	private String consumerId;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	
	
}
