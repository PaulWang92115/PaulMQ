package com.paul.mq.broker;

import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.SubMessage;

//broker 接收到消费者消息后的处理方法，broker 会持有这个接口的实现类
public interface ConsumerMessageListener {
    void processConsumerMessage(SubMessage msg, ChannelData channel);
}
