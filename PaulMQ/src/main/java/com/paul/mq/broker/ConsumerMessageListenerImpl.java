package com.paul.mq.broker;

import com.paul.mq.consumer.ConsumerContext;
import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.RegisterMessage;

/**
 * 
 * broker 接收到 conumser 的订阅消息的 listener 接口的实现类
 * 主要用于将 consumer 加入到 消费者集群中
 *
 */
public class ConsumerMessageListenerImpl implements ConsumerMessageListener{

	public void processConsumerMessage(RegisterMessage msg, ChannelData channel) {
		System.out.println("receive sub message from consumer, exchange:" + msg.getExchange() + " queue:" + msg.getQueue()
				+ " clientId:" + channel.getClientId());
		//
		if(msg.getExchange() == null){
			//没有 exchange， 所以直接使用 队列名
			ConsumerContext.addQueue(msg, channel);
		}else{
			//将 consumer 加入到消费者集群中
			ConsumerContext.addClusters(msg, channel);
		}
		
	}

}
