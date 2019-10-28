package com.paul.mq.consumer;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.paul.mq.entity.ChannelData;
import com.paul.mq.util.NettyUtil;

/**
 * 
 * 消费者集群
 *
 */
public class ConsumerClusters {
	
    private int next = 0;
	//集群 id 是用来区分消费者集群的
	private final String clustersId;
	
	//集群中所有 consumer 订阅的 topic 的 list
	private final List<String> topicList = Collections.synchronizedList(new ArrayList<String>());
	
	// clientId 和 channelData 的对应关系
	private final ConcurrentHashMap<String,ChannelData> channelMap = 
			new ConcurrentHashMap<String,ChannelData>();
	
	//集群中的所有 conumser 的 channelData
	private final List<ChannelData> channelList = Collections.synchronizedList(new ArrayList<ChannelData>());

	public ConsumerClusters(String clustersId) {
		this.clustersId = clustersId;
	}

	public String getClustersId() {
		return clustersId;
	}


	public ConcurrentHashMap<String, ChannelData> getChannelMap() {
		return channelMap;
	}

	public List<ChannelData> getChannelList() {
		return channelList;
	}
	
	public List<String> getTopicList() {
		return topicList;
	}

	/**
	 * 将消费者信息添加到集群中
	 * @param clientId
	 * @param channelData
	 */
	public void addChannelData(String clientId,ChannelData channelData){
		if(null == findChannelData(clientId)){
			channelMap.put(clientId, channelData);
			topicList.add(channelData.getTopic());
			channelList.add(channelData);
		}else{
			System.out.println("consumer already exist in clusters:" + clustersId + " clientId:" + clientId);
		}
	}
	
	public void removeChannelData(final String clientId){
		channelMap.remove(clientId);
		
		Predicate predicate = new Predicate(){
			public boolean evaluate(Object arg0) {
				String id = ((ChannelData)arg0).getClientId();
				return id.compareTo(clientId) == 0;
			}	
		};
		
		ChannelData channelData = (ChannelData) CollectionUtils.find(channelList, predicate);
		if(null != channelData){
			channelList.remove(channelData);
		}
	}
	
    //负载均衡，根据连接到broker的顺序，依次投递消息给消费者。这里的均衡算法直接采用
    //轮询调度（Round-Robin Scheduling），后续可以加入：加权轮询、随机轮询、哈希轮询等等策略。
	public ChannelData nextChannelData(){
		Predicate predicate = new Predicate(){
			public boolean evaluate(Object arg0) {
				ChannelData channelData = (ChannelData)arg0;
				Channel channel = channelData.getChannel();
				return NettyUtil.validateChannel(channel);
			}
		};
		
		CollectionUtils.filter(channelList, predicate);
        return channelList.get(next++ % channelList.size());
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
