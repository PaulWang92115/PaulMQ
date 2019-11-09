package com.paul.mq.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.consumer.ConsumerContext;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.UnRegisterMessage;

public class UnRegisterMessageProcessor implements BrokerProcessor{

	public void messageDispatch(RequestMessage request, ResponseMessage response) {
		//broker server 接收到了 consumer 的取消订阅消息
		UnRegisterMessage unsub = (UnRegisterMessage) request.getMessage();
		//从消费者集群中去掉这个消费者
		ConsumerContext.removeQueue(unsub.getConsumerId());
		ConsumerContext.removeClusters(unsub.getConsumerId());
	}

	public void setHookProducer(ProducerMessageListener processProducer) {
	
	}

	public void setHookConsumer(ConsumerMessageListener processConsumer) {
		
	}

	public void setChannelHandler(ChannelHandlerContext channelHandler) {
		
	}

}
