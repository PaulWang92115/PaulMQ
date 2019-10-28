package com.paul.mq.consumer;

import java.util.UUID;

import com.google.common.base.Joiner;
import com.paul.mq.common.MQServer;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.entity.SubMessage;
import com.paul.mq.entity.UnSubMessage;
import com.paul.mq.netty.NettyConnector;


public class PaulMQConsumer extends NettyConnector implements MQServer{
	
	// broker server 远程地址
	private String host;
	
	private Integer port;
	
	// 消费者订阅的 topic
	private String topic;
	
	private ReceiveMessageCallBack messageCakkBack;;
	
    private String defaultClusterId = "PaulMQConsumerClusters";
    private String clusterId = "";
    private String consumerId = "";
    
    private boolean isRunning = false;

	public PaulMQConsumer(String host,Integer port,String clusterId, String topic,ReceiveMessageCallBack messageCakkBack) {
		//通过父类构建 netty 连接
		super(host,port);
		this.host = host;
		this.port = port;
		this.topic = topic;
		this.clusterId = clusterId;
		this.messageCakkBack = messageCakkBack;
	}

	public void init() {
		//设置 nettyclient 的 handler
		super.getNettyClient().setMessageHandle(new ConsumerNettyHandler(this,messageCakkBack));
        Joiner joiner = Joiner.on("@").skipNulls();
        consumerId = joiner.join((clusterId.equals("") ? defaultClusterId : clusterId), topic, UUID.randomUUID().toString());
		
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
		
		UnSubMessage unsub = new UnSubMessage();
		unsub.setConsumerId(consumerId);
		RequestMessage request = new RequestMessage();
        request.setMessageType(MessageType.UNSUBSCRIBE);
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
		requestMessage.setMessageType(MessageType.SUBSCRIBE);
		requestMessage.setSourceType(SourceType.CONSUMER);
		
		SubMessage sub = new SubMessage();
		sub.setClusterId((clusterId.equals("") ? defaultClusterId : clusterId));
		sub.setTopic(topic);
		sub.setConsumerId(consumerId);
		
		requestMessage.setMessage(sub);
		sendAsyncMessage(requestMessage);
	}

}
