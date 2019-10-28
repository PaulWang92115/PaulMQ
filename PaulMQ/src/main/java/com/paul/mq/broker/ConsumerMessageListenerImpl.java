package com.paul.mq.broker;

import com.paul.mq.consumer.ConsumerClustersContext;
import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.SubMessage;

/**
 * 
 * broker 接收到 conumser 的订阅消息的 listener 接口的实现类
 * 主要用于将 consumer 加入到 消费者集群中
 *
 */
public class ConsumerMessageListenerImpl implements ConsumerMessageListener{

	public void processConsumerMessage(SubMessage msg, ChannelData channel) {
		System.out.println("receive sub message from consumer, groupId:" + msg.getClusterId() + " topoc:" + msg.getTopic()
				+ " clientId:" + channel.getClientId());
		channel.setTopic(msg.getTopic());
		//将 consumer 加入到消费者集群中
		ConsumerClustersContext.addClusters(msg.getClusterId(), channel);
		
	}

}
