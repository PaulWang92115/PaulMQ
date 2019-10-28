package com.paul.mq.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.consumer.ConsumerClustersContext;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.UnSubMessage;

public class UnSubscribeMessageProcessor implements BrokerProcessor{

	public void messageDispatch(RequestMessage request, ResponseMessage response) {
		//broker server 接收到了 consumer 的取消订阅消息
		UnSubMessage unsub = (UnSubMessage) request.getMessage();
		//从消费者集群中去掉这个消费者
		ConsumerClustersContext.removeClusters(unsub.getConsumerId());
	}

	public void setHookProducer(ProducerMessageListener processProducer) {
	
	}

	public void setHookConsumer(ConsumerMessageListener processConsumer) {
		
	}

	public void setChannelHandler(ChannelHandlerContext channelHandler) {
		
	}

}
