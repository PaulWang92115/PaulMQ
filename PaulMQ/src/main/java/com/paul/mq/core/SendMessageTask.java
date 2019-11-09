package com.paul.mq.core;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;

import com.paul.mq.broker.SendMessageLauncher;
import com.paul.mq.consumer.ConsumerClusters;
import com.paul.mq.consumer.ConsumerContext;
import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.ConsumerAckMessage;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.MessageDispatchTask;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;
import com.paul.mq.entity.WorkMode;
import com.paul.mq.util.NettyUtil;

public class SendMessageTask implements Callable<Void>{
	
	private MessageDispatchTask[] tasks;
	private Phaser phaser = null;
	private SendMessageLauncher launcher = SendMessageLauncher.getInstance();
	
	public SendMessageTask(Phaser phaser,MessageDispatchTask[] tasks){
		this.phaser = phaser;
		this.tasks = tasks;
	}

	@Override
	public Void call() throws Exception {
		for(MessageDispatchTask task:tasks){
			ChannelData channel = null;
			Message msg = task.getMessage();
			WorkMode mode = msg.getMode();
			if(mode.equals(WorkMode.WORKER_FANOUT) || mode.equals(WorkMode.WORKER_ROBIN)){
				channel = ConsumerContext.getChannel(task.getConsumerId());
			}else{
				if(ConsumerContext.getByClusters(msg.getExchange().getName())!=null){
					ConsumerClusters clu = ConsumerContext.getByClusters(msg.getExchange().getName());
					channel = clu.getChannelMap().get(task.getConsumerId());
				}
			}
			if(channel!=null){
				ResponseMessage response  = new ResponseMessage();
				response.setSourceType(SourceType.BROKER);
				response.setMessageType(MessageType.MESSAGE);
				response.setMessage(msg);
				response.setMsgId(UUID.randomUUID().toString());
				if(!NettyUtil.validateChannel(channel.getChannel())){
					// channel 已经不可用，需要再 ConsumerContext 中去掉他
					continue;
				}
				
				RequestMessage request = (RequestMessage) launcher.launcher(channel.getChannel(), response);
				
				//从消费者返回的信息中获取消费者应答, 记录与统计
				ConsumerAckMessage result = (ConsumerAckMessage) request.getMessage();
			}
		}
		//若干个并行的线程共同到达统一的屏障点之后，再进行消息统计，把数据最终汇总给JMX
		phaser.arriveAndAwaitAdvance();
		return null;
	}

}
