package com.paul.mq.broker;

import java.util.concurrent.atomic.AtomicReference;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.processor.BrokerProcessorContext;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.netty.NettyHandler;

/*
 * 服务端的 nettyhandler 只有一个实例，此处需要注意多线程情况处理
 * 
 */

@ChannelHandler.Sharable
public class BrokerNettyHandler extends NettyHandler<Object>{
	
	//borker 服务端接收到生产者消费者消息后的处理方法需要做线程安全处理，可以再方法加 synchronized
	//此处使用 CAS 获取对象
	private AtomicReference<ProducerMessageListener> processProducer;
	private AtomicReference<ConsumerMessageListener> processConsumer;
	
	
	public BrokerNettyHandler() {
		super.setSubHandler(this);
	}
	
	public BrokerNettyHandler buildProcessProducer(ProducerMessageListener processProducer){
		this.processProducer = new AtomicReference<ProducerMessageListener>(processProducer);
		return this;
	}
	
	public BrokerNettyHandler buildProcessConsumer(ConsumerMessageListener processConsumer){
		this.processConsumer = new AtomicReference<ConsumerMessageListener>(processConsumer);
		return this;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, Object msg) {
		//服务端接收到的 requestMessage
		RequestMessage request = (RequestMessage)msg;
		
		ResponseMessage response = new ResponseMessage();
		response.setMsgId(request.getMsgId());
		response.setSourceType(SourceType.BROKER);
		
		//交给 processor 去处理
		BrokerProcessorContext context = new BrokerProcessorContext(request,response,ctx);
		context.setProcessConsumer(processConsumer.get());
		context.setProcessProducer(processProducer.get());
		context.invoke();
		
	}

	@Override
	public void beforeHandle(Object msg) {
		System.out.println("broker server receive message, start process ........");
	}

	@Override
	public void afterHandle(Object msg) {
		System.out.println("broker server receive message, start process ........");
	}
	
	

}
