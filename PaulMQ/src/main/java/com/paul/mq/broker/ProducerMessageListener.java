package com.paul.mq.broker;

import com.paul.mq.entity.Message;

import io.netty.channel.Channel;

//broker 接收到生产者消息后的处理方法，broker 会持有这个接口的实现类
public interface ProducerMessageListener {
    void processProducerMessage(Message msg, String requestId, Channel channel);
}
