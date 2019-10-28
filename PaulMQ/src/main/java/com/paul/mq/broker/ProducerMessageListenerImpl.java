package com.paul.mq.broker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AnyPredicate;

import io.netty.channel.Channel;

import com.google.common.base.Joiner;
import com.paul.mq.consumer.ConsumerClusters;
import com.paul.mq.consumer.ConsumerClustersContext;
import com.paul.mq.core.AckMessageCache;
import com.paul.mq.core.AckMessageTaskQueue;
import com.paul.mq.core.MessageTaskQueue;
import com.paul.mq.core.ProducerCache;
import com.paul.mq.core.SemaphoreCache;
import com.paul.mq.core.SemaphoreConfig;
import com.paul.mq.entity.Message;
import com.paul.mq.entity.MessageDispatchTask;
import com.paul.mq.entity.ProducerAckMessage;
/**
 * broker server 接收到了生产者发送的消息，后续的处理工作交给这个类
 * @author swang18
 *
 */
public class ProducerMessageListenerImpl implements ProducerMessageListener{
	
    private List<ConsumerClusters> clustersSet = new ArrayList<ConsumerClusters>();
	
	/**
	 * 
	 * @param topic
	 * @param list
	 */
	private List<ConsumerClusters> filterByTopic(final String topic,List<ConsumerClusters> list){
		Predicate p = new Predicate(){
			public boolean evaluate(Object arg0) {
				ConsumerClusters clusters = (ConsumerClusters)arg0;
				return clusters.getTopicList().contains(topic);
			}
		};
		AnyPredicate any  = new AnyPredicate(new Predicate[]{p});
		
		Closure join = new Closure(){
			public void execute(Object input) {
				if(input instanceof ConsumerClusters){
					clustersSet.add((ConsumerClusters)input);
				}
			}
		};
		
		Closure ignore = new Closure(){
			public void execute(Object input) {
			}
		};
		
		Closure ifClosure = ClosureUtils.ifClosure(any, join, ignore);
		CollectionUtils.forAllDo(list, ifClosure);
		
		return list;
	}
	
	/**
	 * 查看是否有对应的消费者
	 * @param msg
	 * @param requestId
	 * @return
	 */
	private boolean checkCluster(Message msg,String requestId){
		if(clustersSet.size() <= 0){
			System.out.println("No active consumer exist for this topic: " + msg.getTopic());
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
	
	private void dispatchMessage(Message msg,String topic){
		List<MessageDispatchTask> list = new ArrayList<>();
		for(int i=0;i<clustersSet.size();i++){
			MessageDispatchTask task = new MessageDispatchTask();
			task.setClusters(clustersSet.get(i).getClustersId());
			task.setTopic(topic);
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
		List<ConsumerClusters> l = ConsumerClustersContext.getByTopic(msg.getTopic());
		l = filterByTopic(msg.getTopic(),l);
		if(checkCluster(msg,requestId)){
			dispatchMessage(msg,msg.getTopic());
			//taskAck
			taskAck(msg,requestId);
			clustersSet.clear();
		}else{
			//没有消费者，直接返回
			return;
		}
		
	}

}
