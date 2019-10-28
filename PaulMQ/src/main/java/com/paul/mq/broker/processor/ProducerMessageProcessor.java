package com.paul.mq.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
/**
 * 
 * borker server 接收到生产者得消息后得处理类
 *
 */
public class ProducerMessageProcessor implements BrokerProcessor{
	
	private ProducerMessageListener processProducer;
	private ChannelHandlerContext channelHandler;

	public void messageDispatch(RequestMessage request, ResponseMessage response) {
		//得到生产者发送得消息
		Message message = (Message) request.getMessage();
		//生产者发送消息后得回调方法
		processProducer.processProducerMessage(message, request.getMsgId(), channelHandler.channel());
	}

	public void setHookProducer(ProducerMessageListener processProducer) {
		this.processProducer = processProducer;
	}

	public void setHookConsumer(ConsumerMessageListener processConsumer) {
		
	}

	public void setChannelHandler(ChannelHandlerContext channelHandler) {
		this.channelHandler = channelHandler;
	}

}
