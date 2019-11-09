package com.paul.mq.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AnyPredicate;

import io.netty.channel.Channel;

import com.google.common.base.Joiner;
import com.paul.mq.consumer.ConsumerClusters;
import com.paul.mq.consumer.ConsumerContext;
import com.paul.mq.core.AckMessageCache;
import com.paul.mq.core.AckMessageTaskQueue;
import com.paul.mq.core.MessageTaskQueue;
import com.paul.mq.core.ProducerCache;
import com.paul.mq.core.SemaphoreCache;
import com.paul.mq.core.SemaphoreConfig;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.MessageDispatchTask;
import com.paul.mq.entity.ProducerAckMessage;
import com.paul.mq.entity.WorkMode;
/**
 * broker server 接收到了生产者发送的消息，后续的处理工作交给这个类
 * @author swang18
 *
 */
public class ProducerMessageListenerImpl implements ProducerMessageListener{
	
    private List<String> consumerSet = new ArrayList<String>();
	
	/**
	 * 查看是否有对应的消费者
	 * @param msg
	 * @param requestId
	 * @return
	 */
	private boolean checkCluster(Message msg,String requestId){
		if(consumerSet.size() <= 0){
			System.out.println("No active consumer exist for this queue: " + msg.getQueue() + "for exchange: " + msg.getExchange());
			//给生产者返回信息
			ProducerAckMessage ack = new ProducerAckMessage();
			ack.setMsgId(msg.getMsgId());
			ack.setAck(requestId);
			ack.setStatus(ProducerAckMessage.SUCCESS);
			//放入队列中
			AckMessageTaskQueue.pushAck(ack);
			//释放生产者应答消息信号量
			SemaphoreCache.release(SemaphoreConfig.ACKMESSAGE.value);
			return false;
		}else{
			return true;
		}
	}
	
	private void dispatchMessage(Message msg){
		List<MessageDispatchTask> list = new ArrayList<>();
		for(int i=0;i<consumerSet.size();i++){
			MessageDispatchTask task = new MessageDispatchTask();
			task.setConsumerId(consumerSet.get(i));
			task.setMessage(msg);
			list.add(task);
		}
		MessageTaskQueue.pushMessages(list);
		//放入 task 池中
		//释放多个信号量
		for(int i=0;i<list.size();i++){
			SemaphoreCache.release(SemaphoreConfig.PRODUCERMESSAGE.value);
		}
	}
	
    private void taskAck(Message msg, String requestId) {
        try {
            Joiner joiner = Joiner.on("@").skipNulls();
            String key = joiner.join(requestId, msg.getMsgId());
            AckMessageCache.getInsance().appendMessage(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	
	@Override
	public void processProducerMessage(Message msg, String requestId,
			Channel channel) {
		ProducerCache.add(requestId, channel);
		WorkMode mode = msg.getMode();
		if(mode.equals(WorkMode.WORKER_ROBIN)){
			String consumerId = ConsumerContext.nextConsumer(msg.getQueue());
			if(null!=consumerId){
				consumerSet.add(consumerId);
			}
		}else if(mode.equals(WorkMode.WORKER_FANOUT)){
			List<String> res = ConsumerContext.getQueue(msg.getQueue());
			if(res != null){
				consumerSet.addAll(res);
			}
		}else if(mode.equals(WorkMode.EXCHANGE_FANOUT)){
			consumerSet.addAll(ConsumerContext.getByClusters(msg.getExchange().getName()).getQueuelist());
		}else if(mode.equals(WorkMode.EXCHANGE_DIRECT)){
			Map<String,String> m = ConsumerContext.getByClusters(msg.getExchange().getName()).getRegrexMap();
			for(Entry<String,String> entry:m.entrySet()){
				if(entry.getValue().equals(msg.getExchange().getRegrex())){
					consumerSet.add(entry.getKey());
				}
			}
		}else if(mode.equals(WorkMode.EXCHANGE_TOPIC)){
			Map<String,String> m = ConsumerContext.getByClusters(msg.getExchange().getName()).getRegrexMap();
			for(Entry<String,String> entry:m.entrySet()){
				String pattern = entry.getValue();
				String producer_rule = msg.getExchange().getRegrex();
				if(Pattern.matches(pattern, producer_rule)){
					consumerSet.add(entry.getKey());
				}
			}
		}else{
			System.out.println("wrong mode !!!");
		}
		if(checkCluster(msg,requestId)){
			dispatchMessage(msg);
			//taskAck
			taskAck(msg,requestId);
			consumerSet.clear();
		}else{
			//没有消费者，直接返回
			return;
		}
		
	}

}
