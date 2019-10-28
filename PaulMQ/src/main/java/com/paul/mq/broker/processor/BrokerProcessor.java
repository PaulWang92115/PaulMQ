package com.paul.mq.broker.processor;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;


public interface BrokerProcessor {
	
    void messageDispatch(RequestMessage request, ResponseMessage response);

    void setHookProducer(ProducerMessageListener processProducer);

    void setHookConsumer(ConsumerMessageListener processConsumer);

    void setChannelHandler(ChannelHandlerContext channelHandler);
}
