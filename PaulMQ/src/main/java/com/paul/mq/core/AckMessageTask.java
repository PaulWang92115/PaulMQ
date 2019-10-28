package com.paul.mq.core;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import com.paul.mq.entity.ProducerAckMessage;

public class AckMessageTask implements Callable<Long>{

	CyclicBarrier barrier = null;
	String[] messages = null;
	private final AtomicLong count = new AtomicLong(0);
	
	
	public AckMessageTask(CyclicBarrier barrier,String[] messages){
		this.barrier = barrier;
		this.messages = messages;
	}
	
	@Override
	public Long call() throws Exception {
		for(int i=0;i<messages.length;i++){
			ProducerAckMessage ack = new ProducerAckMessage();
			// message 的格式式 1@1
			String[] msg = messages[i].split("@");
			ack.setStatus(ProducerAckMessage.SUCCESS);
			ack.setAck(msg[0]);
			ack.setMsgId(msg[1]);
			count.incrementAndGet();
			AckMessageTaskQueue.pushAck(ack);
			SemaphoreCache.release(SemaphoreConfig.ACKMESSAGE.value);
		}
		barrier.await();
		return count.get();
	}

}
