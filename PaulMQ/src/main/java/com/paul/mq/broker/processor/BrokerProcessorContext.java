package com.paul.mq.broker.processor;

import io.netty.channel.ChannelHandlerContext;

import com.paul.mq.broker.ConsumerMessageListener;
import com.paul.mq.broker.ProducerMessageListener;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;

public class BrokerProcessorContext {
    
    private RequestMessage request;
    private ResponseMessage response;
    private ChannelHandlerContext channelHandler;
    
    
    private ProducerMessageListener processProducer;
    private ConsumerMessageListener processConsumer;
    private BrokerProcessor processor;
    
    
    public BrokerProcessorContext(RequestMessage request,ResponseMessage response,ChannelHandlerContext channelHandler){
    	this.request = request;
    	this.response = response;
    	this.channelHandler = channelHandler;
    }


	public void setProcessProducer(ProducerMessageListener processProducer) {
		this.processProducer = processProducer;
	}

	public void setProcessConsumer(ConsumerMessageListener processConsumer) {
		this.processConsumer = processConsumer;
	}
    
	public void invoke(){
		switch(request.getMessageType()){
			case MESSAGE:
				//收到 message 类型得消息后对应得 processor
				processor = request.getSourceType() == SourceType.PRODUCER ? new ProducerMessageProcessor():new ConsumerMessageProcessor();
				break;
			case SUBSCRIBE:
				processor = new SubscribeMessageProcessor();
				break;
			case UNSUBSCRIBE:
				processor = new UnSubscribeMessageProcessor();
				break;
			default:
				break;
		}
		
		processor.setChannelHandler(channelHandler);
		processor.setHookConsumer(processConsumer);
		processor.setHookProducer(processProducer);
		processor.messageDispatch(request, response);
	}
    

}
