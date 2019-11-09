package com.paul.mq.producer;

import java.util.concurrent.atomic.AtomicLong;

import com.paul.mq.common.MQServer;
import com.paul.mq.entity.Exchange;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.ProducerAckMessage;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.entity.WorkMode;
import com.paul.mq.netty.NettyConnector;

/**
 * 
 * 生产者核心类，因为要与 broker server 建立连接，所以继承 NettyConnector
 *
 */
public class PaulMQProducer extends NettyConnector implements MQServer{

	//生产者是否连接到了 borker 服务端
	private boolean isConnect = false;
	//是否正在运行
	private boolean isRunning = false;
	private String host;
	private Integer port;
	
	//producer 实例 的 messageId
	private AtomicLong msgId = new AtomicLong(0L);
	
	public PaulMQProducer(String host, Integer port) {
		super(host, port);
		this.host = host;
		this.port = port;
	}

	public void init() {
		//设置 nettyClient 的 handler
		super.getNettyClient().setMessageHandle(new ProducerNettyHandler(this));
	}

	public void start() {
		//建立与 broker server 的连接
		super.getNettyClient().start();
		isConnect = true;
		isRunning = true;
	}

	public void stop() {
		if(isRunning){
			isRunning = false;
			super.getNettyClient().stop();
			super.closeNettyClientPool();
		}
		
	}
	
	public ProducerAckMessage produce(WorkMode mode,String queue,Exchange exchange,Message message){
		if(!isConnect || !isRunning){
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setStatus(ProducerAckMessage.FAIL);
            return ack;
		}
		String id = String.valueOf(msgId.incrementAndGet());
		message.setQueue(queue);
		message.setExchange(exchange);
		message.setMode(mode);
		message.setTimeStamp(System.currentTimeMillis());
		message.setMsgId(id);
		
		//封装到 netty 传输的 request
		RequestMessage request = new RequestMessage();
		request.setMsgId(id);
		request.setMessage(message);
		request.setMessageType(MessageType.MESSAGE);
		request.setSourceType(SourceType.PRODUCER);
		
		ResponseMessage response = (ResponseMessage)sendAsyncMessage(request);
		
        if (response == null) {
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setStatus(ProducerAckMessage.FAIL);
            return ack;
        }

        ProducerAckMessage result = (ProducerAckMessage) response.getMessage();
        return result;
	}

}
