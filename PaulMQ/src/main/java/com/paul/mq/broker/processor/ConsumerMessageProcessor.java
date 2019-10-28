package com.paul.mq.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.broker.SendMessageLauncher;
import com.paul.mq.common.CallBack;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;

public class ConsumerMessageProcessor implements BrokerProcessor{

	public void messageDispatch(RequestMessage request, ResponseMessage response) {
		//broker server 接收到了 consumer 得应答消息，表明消费者接收到了消息
        String key = response.getMsgId();
        if (SendMessageLauncher.getInstance().trace(key)) {
            CallBack<Object> future = SendMessageLauncher.getInstance().detach(key);
            if (future == null) {
                return;
            } else {
                future.setMessageResult(request);
            }
        } else {
            return;
        }
	}

	public void setHookProducer(ProducerMessageListener processProducer) {
		
	}

	public void setHookConsumer(ConsumerMessageListener processConsumer) {
		
	}

	public void setChannelHandler(ChannelHandlerContext channelHandler) {
		
	}

}
