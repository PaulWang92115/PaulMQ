package com.paul.mq.broker;

import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.RegisterMessage;
import com.paul.mq.entity.WorkMode;

//broker 接收到消费者消息后的处理方法，broker 会持有这个接口的实现类
public interface ConsumerMessageListener {
    void processConsumerMessage(RegisterMessage msg, ChannelData channel);
}
