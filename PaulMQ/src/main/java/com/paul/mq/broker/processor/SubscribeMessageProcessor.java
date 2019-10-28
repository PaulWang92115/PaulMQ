package com.paul.mq.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SubMessage;

public class SubscribeMessageProcessor implements BrokerProcessor{
	
	private ConsumerMessageListener processConsumer;
	private ChannelHandlerContext channelHandler;

	public void messageDispatch(RequestMessage request, ResponseMessage response) {
		//broker server 接收到 消费者得订阅消息后得处理方法
		SubMessage sub = (SubMessage) request.getMessage();
		String consumerId = sub.getConsumerId();
		ChannelData channelData = new ChannelData(channelHandler.channel(),consumerId);
		processConsumer.processConsumerMessage(sub, channelData);
		//服务端给消费者应答
		response.setMessageType(MessageType.CONSUMERACK);
		channelHandler.writeAndFlush(response);
	}

	public void setHookProducer(ProducerMessageListener processProducer) {
		
	}

	public void setHookConsumer(ConsumerMessageListener processConsumer) {
		this.processConsumer = processConsumer;
	}

	public void setChannelHandler(ChannelHandlerContext channelHandler) {
		this.channelHandler = channelHandler;
	}

}
