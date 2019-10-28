package com.paul.mq.core;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;

import com.paul.mq.broker.SendMessageLauncher;
import com.paul.mq.consumer.ClustersState;
import com.paul.mq.consumer.ConsumerClustersContext;
import com.paul.mq.entity.ChannelData;
import com.paul.mq.entity.ConsumerAckMessage;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.MessageDispatchTask;
import com.paul.mq.entity.MessageType;
import com.paul.mq.entity.RequestMessage;
import com.paul.mq.entity.ResponseMessage;
import com.paul.mq.entity.SourceType;
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
			Message msg = task.getMessage();
			
			if(ConsumerClustersContext.getByClusters(task.getClusters())!=null){
				ChannelData channel = ConsumerClustersContext.getByClusters(task.getClusters()).nextChannelData();
				
				ResponseMessage response  = new ResponseMessage();
				response.setSourceType(SourceType.BROKER);
				response.setMessageType(MessageType.MESSAGE);
				response.setMessage(msg);
				response.setMsgId(UUID.randomUUID().toString());
				try{
					if(!NettyUtil.validateChannel(channel.getChannel())){
						ConsumerClustersContext.setClustersState(task.getClusters(), ClustersState.NETWORKERR);
						continue;
					}
					
					RequestMessage request = (RequestMessage) launcher.launcher(channel.getChannel(), response);
					
					//从消费者返回的信息中获取消费者应答
					ConsumerAckMessage result = (ConsumerAckMessage) request.getMessage();
					
					if(result.getStatus() == ConsumerAckMessage.SUCCESS){
						ConsumerClustersContext.setClustersState(task.getClusters(), ClustersState.SUCCESS);
					}
				}catch(Exception e){
					ConsumerClustersContext.setClustersState(task.getClusters(), ClustersState.ERROR);
				}
			}
		}
		//若干个并行的线程共同到达统一的屏障点之后，再进行消息统计，把数据最终汇总给JMX
		phaser.arriveAndAwaitAdvance();
		return null;
	}

}
