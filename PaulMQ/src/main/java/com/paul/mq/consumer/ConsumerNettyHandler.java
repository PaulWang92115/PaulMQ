package com.paul.mq.consumer;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.entity.ConsumerAckMessage;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.netty.NettyConnector;
import com.paul.mq.netty.NettyHandler;

public class ConsumerNettyHandler extends NettyHandler<Object>{

	public ConsumerNettyHandler(NettyConnector connector,ReceiveMessageCallBack callBack) {
		super(connector,callBack);
		super.setSubHandler(this);
	}

	@Override
	public void handle(ChannelHandlerContext ctx, Object msg) {
		System.out.println("consumer receive message from broker server, in process ........");
		//服务端发送给客户端的叫 response，客户端发送给服务端的叫 request
		
		//此处可能收到两种类型的消息
		//1.consumer 给服务端发送订阅消息后，服务端的返回
		//2.broker server 将 producer 的 message 传给 consuemr
		ResponseMessage response = ((ResponseMessage) msg);
        String key = response.getMsgId();
		if(!nettyClient.checkCallBack(key) && callBack!=null){
			//condition1, callback 里面没有，说明不是 consumer 主动发送给 broker 的消息，所以为 condition 2
			
			ConsumerAckMessage result = null;
	        if (response.getMessage() instanceof Message) {
	        	callBack.onCallBack((Message)response.getMessage());
	        	result = new ConsumerAckMessage();
	        	result.setAck("SUCCESS");
	        	result.setStatus(200);
	        	result.setMsgId(((Message)response.getMessage()).getMsgId());
	        } 
			
			//消费端收到了 producer 发送的信息，要返回应答给服务端，表明消费端接收消息成功
			if(result != null){
				RequestMessage request = new RequestMessage();
				request.setMsgId(key);
				request.setMessage(result);
				request.setMessageType(MessageType.MESSAGE);
				request.setSourceType(SourceType.CONSUMER);
				
				ctx.writeAndFlush(request);
			}
		}else{
			//condition1
			System.out.println("consumer register successful");
		}
	}

	@Override
	public void beforeHandle(Object msg) {
		System.out.println("consumer receive message from broker server, start process ........");
	}

	@Override
	public void afterHandle(Object msg) {
		System.out.println("consumer receive message from broker server, end process");
	}
	
	

}
