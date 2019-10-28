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

/**
 * 这个线程负责从应答消息队列中获取应答消息，并发送
 * @author swang18
 *
 */
public class AckPullMessageController implements Callable<Void>{

	private volatile boolean stoped = false;
	
	public void stop(){
		stoped = false;
	}
	
    public boolean isStoped() {
        return stoped;
    }
	
	@Override
	public Void call() throws Exception {
		while(!stoped){
			SemaphoreCache.acquire(SemaphoreConfig.ACKMESSAGE.value);
            ProducerAckMessage ack = AckMessageTaskQueue.getAck();
            String requestId = ack.getAck();
            ack.setAck("");
            
            Channel channel = ProducerCache.remove(requestId);
            if(NettyUtil.validateChannel(channel)){
            	ResponseMessage response = new ResponseMessage();
            	response.setMsgId(requestId);
            	response.setSourceType(SourceType.BROKER);
            	response.setMessage(ack);
            	response.setMessageType(MessageType.PRODUCERACK);
            	
            	channel.writeAndFlush(response);
            }
		}
		return null;
	}

}
