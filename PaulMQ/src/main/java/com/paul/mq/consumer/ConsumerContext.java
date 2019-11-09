package com.paul.mq.consumer;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.RegisterMessage;
import com.paul.mq.util.NettyUtil;


/*
 * 消费者集群的管理类
 * 
 */
public class ConsumerContext {
	
	private static int next = 0;
	
	// 没有 exchange 的 concumer， <queue, List<ConcumerId>>
	private static final Map<String,List<String>> queueList = new ConcurrentHashMap<String,List<String>>();
	// 没有 exchange 的 concumer clientId 和 channelData 的对应关系
	private final static ConcurrentHashMap<String,ChannelData> queuechannelMap = 
			new ConcurrentHashMap<String,ChannelData>();
	
	private static final CopyOnWriteArrayList<ClustersRelation> relationList = new CopyOnWriteArrayList<ClustersRelation>();
	
	
	/**
	 * 根据 exchangeName 获取 consumerClusters 实例
	 * @param exchangeName
	 * @return
	 */
	public static ConsumerClusters getByClusters(final String exchangeName){
		Predicate predicate = new Predicate(){

			public boolean evaluate(Object arg0) {
				String exchange = ((ClustersRelation) arg0).getExchangeName();
				return exchange.compareTo(exchangeName) == 0;
			}
			
		};
		
        Iterator iterator = new FilterIterator(relationList.iterator(), predicate);

        ClustersRelation relation = null;
        while (iterator.hasNext()) {
            relation = (ClustersRelation) iterator.next();
            break;
        }
        return (relation != null) ? relation.getClusters() : null;
	}
	

	public static void addClusters(RegisterMessage msg, ChannelData channelData){
		ConsumerClusters clu = getByClusters(msg.getExchange().getName());
		if(null == clu){
			ConsumerClusters clusters = new ConsumerClusters(msg.getExchange().getName());
			clusters.addChannelData(channelData.getClientId(), channelData, msg.getExchange());
			relationList.add(new ClustersRelation(msg.getExchange().getName(),clusters));
		}else{
			//channelData 对应的 clientid 已经存在集群中了，更新 channelData 信息
			clu.removeChannelData(channelData.getClientId());
			clu.addChannelData(channelData.getClientId(), channelData, msg.getExchange());
		}
	}
	
	public static void removeClusters(String clientId){
		for(int i=0;i<relationList.size();i++){
			ConsumerClusters clu = relationList.get(i).getClusters();
			
			if(clu.findChannelData(clientId)!=null){
				clu.removeChannelData(clientId);
			}
			//没有活跃的 consuemr 了，再关系表中去掉这个 cluster
			if(clu.getChannelMap().size() == 0){
                relationList.remove(clu);
			}
		}
	}
	
	
	public static List<String> getQueue(String queueName){
		for(Entry entry:queueList.entrySet()){
			System.out.println("key:" + entry.getKey());
			List<String> l = (List<String>)entry.getValue();
			System.out.println("value:" + l.get(0));	
		}
		return queueList.get(queueName);
	}
	
	public static ChannelData getChannel(String queueName){
		return queuechannelMap.get(queueName);
	}
	
	public static void addQueue(RegisterMessage msg, ChannelData channelData){
		List<String> l = getQueue(msg.getQueue());
		
		if(null==l){
			l = new ArrayList<String>();
		}
		if(!l.contains(msg.getConsumerId())){
			l.add(msg.getConsumerId());
		}
		queueList.put(msg.getQueue(), l);
		queuechannelMap.put(msg.getConsumerId(),channelData);
	}
	
	public static void removeQueue(String clientId){
		List<String> queueList = getQueue(clientId);
		if(null != queueList){
			queueList.remove(clientId);
		}
		queuechannelMap.remove(clientId);
	}
	
  //负载均衡，根据连接到broker的顺序，依次投递消息给消费者。这里的均衡算法直接采用
  //轮询调度（Round-Robin Scheduling），后续可以加入：加权轮询、随机轮询、哈希轮询等等策略。
	public static String nextConsumer(String queueName){
		List<String> l = getQueue(queueName);
		if(null == l){
			return null;
		}else{
			return l.get(next++%l.size());
		}
	}

}
