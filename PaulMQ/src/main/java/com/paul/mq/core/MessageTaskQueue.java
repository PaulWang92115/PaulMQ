package com.paul.mq.core;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.paul.mq.entity.MessageDispatchTask;
/**
 * 存放生产者发送消息的队列
 * @author swang18
 *
 */
public class MessageTaskQueue {
	private static ConcurrentLinkedQueue<MessageDispatchTask> ackQueue = new ConcurrentLinkedQueue<MessageDispatchTask>();
	
	public static boolean pushMessage(MessageDispatchTask message){
		return ackQueue.offer(message);
	}
	
	public static boolean pushMessages(List<MessageDispatchTask> messages){
		return ackQueue.addAll(messages);
	}
	
	public static MessageDispatchTask getMessage(){
		return ackQueue.poll();
	}
}
