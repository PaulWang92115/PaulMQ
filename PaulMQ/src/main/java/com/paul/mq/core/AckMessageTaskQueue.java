package com.paul.mq.core;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.paul.mq.entity.ProducerAckMessage;

/**
 * 存放生产者应答消息的队列
 * @author swang18
 *
 */
public class AckMessageTaskQueue {
	
	private static ConcurrentLinkedQueue<ProducerAckMessage> ackQueue = new ConcurrentLinkedQueue<ProducerAckMessage>();
	
	public static boolean pushAck(ProducerAckMessage ack){
		return ackQueue.offer(ack);
	}
	
	public static boolean pushAcks(List<ProducerAckMessage> acks){
		return ackQueue.addAll(acks);
	}
	
	public static ProducerAckMessage getAck(){
		return ackQueue.poll();
	}
}
