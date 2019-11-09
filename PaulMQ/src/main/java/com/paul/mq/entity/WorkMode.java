package com.paul.mq.entity;

public enum WorkMode {
	WORKER_ROBIN("WORKER_ROBIN"),
	WORKER_FANOUT("WORKER_FANOUT"),
	EXCHANGE_FANOUT("EXCHANGE_FANOUT"),
	EXCHANGE_DIRECT("EXCHANGE_DIRECT"),
	EXCHANGE_TOPIC("EXCHANGE_TOPIC");
	
	private String mode;
	WorkMode(String mode){
		this.mode = mode;
	}
	
}
