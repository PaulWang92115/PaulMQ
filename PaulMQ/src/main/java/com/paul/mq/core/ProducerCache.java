package com.paul.mq.core;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ProducerCache {
	
	private static ConcurrentHashMap<String,Channel> producerMap = new ConcurrentHashMap<String, Channel>();
	
	public static void add(String requestId, Channel channel){
		producerMap.put(requestId, channel);
	}
	
	public static Channel remove(String requestId){
		Channel channel = producerMap.remove(requestId);
		return channel;
	}

}
