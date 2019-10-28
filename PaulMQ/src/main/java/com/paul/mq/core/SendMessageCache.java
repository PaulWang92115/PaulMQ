package com.paul.mq.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import org.apache.commons.lang3.tuple.Pair;

import com.paul.mq.entity.MessageDispatchTask;

public class SendMessageCache extends MessageCache<MessageDispatchTask>{

	/**
	 * 多线程工具类，表示阶段器，用来解决多个线程分阶段共同完成任务的情景，
	 * 作用比 CountDownLatch 和 CyclicBarrier 更加灵活
	 */
    private Phaser phaser = new Phaser(0);
    
    private SendMessageCache(){};
    
    private static SendMessageCache instance;
    
    public synchronized static SendMessageCache getInstance(){
    	if(null == instance){
    		instance = new SendMessageCache();
    	}
    	return instance;
    }
    
    

	@Override
	public void parallelDispatch(LinkedList<MessageDispatchTask> list) {
		List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        int startPosition = 0;
        Pair<Integer, Integer> pair = calculateBlocks(list.size(), list.size());
        int numberOfThreads = pair.getRight();
        int blocks = pair.getLeft();
        
        for(int i=0;i<numberOfThreads;i++){
        	MessageDispatchTask[] task = new MessageDispatchTask[blocks];
        	phaser.register();
        	System.arraycopy(list.toArray(), startPosition, task, 0, blocks);
        	tasks.add(new SendMessageTask(phaser,task));
            startPosition += blocks;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        for(Callable<Void> element:tasks){
        	executor.submit(element);
        }
	}
    
    
}

