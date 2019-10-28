package com.paul.mq.broker;

import io.netty.channel.Channel;

import java.util.concurrent.Callable;

import com.paul.mq.core.AckMessageTaskQueue;
import com.paul.mq.core.ProducerCache;
import com.paul.mq.core.SemaphoreCache;
import com.paul.mq.core.SemaphoreConfig;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.ProducerAckMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.util.NettyUtil;

public class ProducerAckMessageController implements Callable<Void>{
	
	private volatile boolean stoped = false;
	
    public void stop() {
        stoped = true;
    }

    public boolean isStoped() {
        return stoped;
    }

	@Override
	public Void call() throws Exception {
		while(!stoped){
			//线程启动后会停到这，等待应答消息队列释放信号量
			SemaphoreCache.acquire(SemaphoreConfig.ACKMESSAGE.value);
			ProducerAckMessage ack = AckMessageTaskQueue.getAck();
			String requestId = ack.getAck();
			ack.setAck("");
			//从生产者 channel 队列中获取 生产者的 channel，获取的同时要删除
			Channel channel = ProducerCache.remove(requestId);
			if(NettyUtil.validateChannel(channel)){
				ResponseMessage response = new ResponseMessage();
				response.setMsgId(requestId);
				response.setSourceType(SourceType.BROKER);
				response.setMessageType(MessageType.PRODUCERACK);
				response.setMessage(ack);
				
				channel.writeAndFlush(response);
			}
			
		}
		return null;
	}

}
