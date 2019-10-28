package com.paul.mq.core;

public enum SemaphoreConfig {
	
	ACKMESSAGE("ACKMESSAGE"),
	PRODUCERMESSAGE("PRODUCERMESSAGE");
	
	public String value;

	private SemaphoreConfig(String value) {
		this.value = value;
	}
	
	
}
