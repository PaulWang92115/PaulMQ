package com.paul.mq.producer;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.common.CallBack;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.netty.NettyConnector;
import com.paul.mq.netty.NettyHandler;

public class ProducerNettyHandler extends NettyHandler<Object>{

	public ProducerNettyHandler(NettyConnector connector) {
		super(connector);
		//将自己传给 NettyHandler 做代理
		super.setSubHandler(this);
	}

	@Override
	public void handle(ChannelHandlerContext ctx, Object msg) {
		System.out.println("producer receive message from broker server, in process ........");
		// netty 客户端与服务端的通信使用的 RequestMessage 和 ResponseMessage，此处收到的是服务端的返回
		String msgId = ((ResponseMessage)msg).getMsgId();
		if(!nettyClient.checkCallBack(msgId)){
			//如果不存在，说明这个消息已经处理过了
			return;
		}
		//从请求列表中删除,表明这个请求已经处理完毕
		CallBack<Object> callBack = nettyClient.removeCallBack(msgId);
		if(null == callBack){
			return;
		}
		//生产者收到的是 broker 回复的应答消息, 将返回信息填充到 ballback 返回给生产者
		if(null != cause){
			callBack.setReason(cause);
		}else{
			callBack.setMessageResult(msg);
		}
	}

	@Override
	public void beforeHandle(Object msg) {
		System.out.println("producer receive message from broker server, start process ........");
	}

	@Override
	public void afterHandle(Object msg) {
		System.out.println("producer receive message from broker server, end process");
	}
	
	

}
