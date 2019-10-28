package com.paul.mq.consumer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import com.paul.mq.entity.ChannelData;


/*
 * 消费者集群的管理类
 * 
 */
public class ConsumerClustersContext {
	
	private static final CopyOnWriteArrayList<ClustersRelation> relationList = new CopyOnWriteArrayList<ClustersRelation>();
	private static final CopyOnWriteArrayList<ClustersState> statesList = new CopyOnWriteArrayList<ClustersState>();
	
	public static void setClustersState(String clusterId, int state){
		statesList.add(new ClustersState(clusterId,state));
	}
	
	/**
	 * 获取当前 consumer 集群的状态
	 * @param clusterId
	 * @return
	 */
	public static int getClustersState(final String clusterId){
		Predicate predicate = new Predicate(){

			public boolean evaluate(Object arg0) {
				String clusters = ((ClustersState) arg0).getClusters();
				return clusters.compareTo(clusterId) == 0;
			}
			
		};
		
		Iterator iterator = new FilterIterator(statesList.iterator(),predicate);
		ClustersState state = null;
		while(iterator.hasNext()){
			state = (ClustersState) iterator.next();
			break;
		}
		
		return (state != null) ? state.getState():0;
	}
	
	/**
	 * 根据 clusterId 获取 consumerClusters 实例
	 * @param clusterId
	 * @return
	 */
	public static ConsumerClusters getByClusters(final String clusterId){
		Predicate predicate = new Predicate(){

			public boolean evaluate(Object arg0) {
				String clusters = ((ClustersRelation) arg0).getClusterId();
				return clusters.compareTo(clusterId) == 0;
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
	
	/**
	 * 根据 topic 来获取多个 消费者集群
	 * @param topic
	 * @return
	 */
	public static List<ConsumerClusters> getByTopic(String topic){
		List<ConsumerClusters> res = new ArrayList<ConsumerClusters>();
		//遍历 relationList 里面的所有 consumerClusters
		for(int i=0;i<relationList.size();i++){
			List<String> topicList = relationList.get(i).getClusters().getTopicList();
			if(topicList.contains(topic)){
				res.add(relationList.get(i).getClusters());
			}
		}
		return res;
	}
	
	public static void addClusters(String clusterId, ChannelData channelData){
		ConsumerClusters clu = getByClusters(clusterId);
		if(null == clu){
			ConsumerClusters clusters = new ConsumerClusters(clusterId);
			clusters.addChannelData(channelData.getClientId(), channelData);
			relationList.add(new ClustersRelation(clusterId,clusters));
		}else if(clu.findChannelData(channelData.getClientId()) != null){
			//channelData 对应的 clientid 已经存在集群中了，更新 channelData 信息
			clu.removeChannelData(channelData.getClientId());
			clu.addChannelData(channelData.getClientId(), channelData);
		}else{
			String topic = channelData.getTopic();
			boolean touchChannel = clu.getTopicList().contains(topic);
			if(touchChannel){
				clu.addChannelData(channelData.getClientId(), channelData);
			}else{
				//集群没有这个 topic
				clu.getTopicList().clear();
				clu.getChannelMap().clear();
				clu.addChannelData(channelData.getClientId(), channelData);
			}
		}
	}
	
	public static void removeClusters(String clientId){
		for(int i=0;i<relationList.size();i++){
			String clusters = relationList.get(i).getClusterId();
			ConsumerClusters clu = relationList.get(i).getClusters();
			
			if(clu.findChannelData(clientId)!=null){
				clu.removeChannelData(clientId);
			}
			//没有活跃的 consuemr 了，再关系表中去掉这个 cluster
			if(clu.getChannelMap().size() == 0){
                relationList.remove(clusters);
			}
		}
	}

}
