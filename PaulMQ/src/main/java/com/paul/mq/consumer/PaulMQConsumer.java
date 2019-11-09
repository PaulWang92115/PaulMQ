package com.paul.mq.consumer;

import java.util.UUID;

import com.google.common.base.Joiner;
import com.paul.mq.common.MQServer;
import com.paul.mq.entity.Exchange;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.entity.RegisterMessage;
import com.paul.mq.entity.UnRegisterMessage;
import com.paul.mq.netty.NettyConnector;


public class PaulMQConsumer extends NettyConnector implements MQServer{
	
	// broker server 远程地址
	private String host;
	
	private Integer port;
	
	private String queue;
	
	private ReceiveMessageCallBack messageCakkBack;;
	
    private String consumerId = "";
    
    private boolean isRunning = false;
    
    private Exchange exchange;

	public PaulMQConsumer(String host,Integer port,String queue,Exchange exchange,ReceiveMessageCallBack messageCakkBack) {
		//通过父类构建 netty 连接
		super(host,port);
		this.host = host;
		this.port = port;
		this.queue = queue;
		this.exchange = exchange;
		this.messageCakkBack = messageCakkBack;
	}

	public void init() {
		//设置 nettyclient 的 handler
		super.getNettyClient().setMessageHandle(new ConsumerNettyHandler(this,messageCakkBack));
        Joiner joiner = Joiner.on("@").skipNulls();
        consumerId = joiner.join(queue, UUID.randomUUID().toString());
		
	}

	public void start() {
		super.getNettyClient().start();
		register();
		isRunning = true;
	}

	public void stop() {
		if(isRunning){
			unRegister();
		}
	}
	
	private void unRegister() {
		
		UnRegisterMessage unsub = new UnRegisterMessage();
		unsub.setConsumerId(consumerId);
		RequestMessage request = new RequestMessage();
        request.setMessageType(MessageType.UNREGISTER);
        request.setMsgId(UUID.randomUUID().toString());
        request.setMessage(unsub);
        sendAsyncMessage(request);
		
		super.getNettyClient().stop();
		super.closeNettyClientPool();
		isRunning = false;
	}

	private void register(){
		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setMsgId(UUID.randomUUID().toString());
		requestMessage.setMessageType(MessageType.REGISTER);
		requestMessage.setSourceType(SourceType.CONSUMER);
		
		RegisterMessage sub = new RegisterMessage();
		sub.setExchange(exchange);
		sub.setQueue(queue);
		sub.setConsumerId(consumerId);
		
		requestMessage.setMessage(sub);
		sendAsyncMessage(requestMessage);
	}

}
