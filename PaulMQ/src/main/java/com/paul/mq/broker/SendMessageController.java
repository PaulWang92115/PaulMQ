package com.paul.mq.broker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.paul.mq.core.MessageTaskQueue;
import com.paul.mq.core.SemaphoreCache;
import com.paul.mq.core.SemaphoreConfig;
import com.paul.mq.core.SendMessageCache;
import com.paul.mq.entity.MessageDispatchTask;

/**
 * 线程循环监听 MessageTaskQueue，查看是否有生产者将消息放入到 Queue 中，
 * 如果有通过 SendMessage Queue 将其发送出去
 * @author swang18
 *
 */
public class SendMessageController implements Callable<Void>{
	
	private volatile boolean stoped = false;
	
	private AtomicBoolean flushTask = new AtomicBoolean(false);
	
	//多个线程同时操作，使用 Threadlocal 防止出现线程安全问题
	private ThreadLocal<ConcurrentLinkedQueue<MessageDispatchTask>> requestCacheList = new ThreadLocal<ConcurrentLinkedQueue<MessageDispatchTask>>(){
        protected ConcurrentLinkedQueue<MessageDispatchTask> initialValue() {
            return new ConcurrentLinkedQueue<MessageDispatchTask>();
        }
	};
	
	private final Timer timer = new Timer("SendMessageTaskMonitor",true);
	
	public void stop(){
		stoped = true;
	}
	
	public boolean isStoped(){
		return stoped;
	}

	@Override
	public Void call() throws Exception {
		//多个任务一起提交
		int commitNumber = 1; 
		
		// 获取信号量，线程刚启动时 pending 在这
		ConcurrentLinkedQueue<MessageDispatchTask> queue = requestCacheList.get();
		SendMessageCache sendCache = SendMessageCache.getInstance();
		while(!stoped){
			//正常情况下，线程启动都会停到这，因为没有发送消息释放这个信号量
			SemaphoreCache.acquire(SemaphoreConfig.PRODUCERMESSAGE.value);
			MessageDispatchTask task = MessageTaskQueue.getMessage();
			queue.add(task);
			
			if(queue.size() == 0){
				//目前没有任务，让线程休眠一会再接着执行
				Thread.sleep(5000);
				continue;
			}
			//当前任务队列里面有任务，如果答到了 5 个 或者 flushTask 为 true 时可以提交
			if(queue.size() > 0 &&(queue.size() % commitNumber == 0 || flushTask.get() == true)){
				sendCache.commit(queue);
				queue.clear();
				flushTask.compareAndSet(true, false);
			}
			
			//如果只有一个任务，一定时间之后将 flushTask 变为 true，这个逻辑还需要再考虑
			timer.scheduleAtFixedRate(new TimerTask(){

				@Override
				public void run() {
					flushTask.compareAndSet(false, true);
				}
				
			}, 1000, 3000);
		}
		return null;
	}

}
