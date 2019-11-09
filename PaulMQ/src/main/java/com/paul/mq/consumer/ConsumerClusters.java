package com.paul.mq.consumer;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.Exchange;
import com.paul.mq.util.NettyUtil;

/**
 * 
 * 消费者集群
 *
 */
public class ConsumerClusters {

	//集群 id 是用来区分消费者集群的
	private final String exchangeName;
	
	private static final CopyOnWriteArrayList<String> queueList = new CopyOnWriteArrayList<String>();
	
	
	// clientId 和 channelData 的对应关系
	private final ConcurrentHashMap<String,ChannelData> channelMap = 
			new ConcurrentHashMap<String,ChannelData>();
	
	// clientId 和 regrex 的对应关系
	private final ConcurrentHashMap<String,String> regrexMap = 
			new ConcurrentHashMap<String,String>();

	public ConsumerClusters(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	


	public String getExchangeName() {
		return exchangeName;
	}



	public ConcurrentHashMap<String, ChannelData> getChannelMap() {
		return channelMap;
	}
	
	

	public CopyOnWriteArrayList<String> getQueuelist() {
		return queueList;
	}



	public ConcurrentHashMap<String, String> getRegrexMap() {
		return regrexMap;
	}



	/**
	 * 将消费者信息添加到集群中
	 * @param clientId
	 * @param channelData
	 */
	public void addChannelData(String clientId,ChannelData channelData,Exchange exchange){
		if(null == findChannelData(clientId)){
			channelMap.put(clientId, channelData);
			queueList.add(clientId);
			if(null != exchange.getRegrex()){
				regrexMap.put(clientId, exchange.getRegrex());
			}
		}else{
			System.out.println("consumer already exist in exchange:" + exchangeName + " clientId:" + clientId);
		}
	}
	
	public void removeChannelData(final String clientId){
		channelMap.remove(clientId);
		queueList.remove(clientId);
		regrexMap.remove(clientId);
	}
	

	

	/**
	 * 根据 consumerId 查找对应的 channelData
	 * @param clientId
	 * @return
	 */
	public ChannelData findChannelData(String clientId){
		return this.channelMap.get(clientId);
	}
	
	
}
