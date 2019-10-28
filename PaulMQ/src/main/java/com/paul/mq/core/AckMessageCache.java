package com.paul.mq.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;


public class AckMessageCache extends MessageCache<String>{
	
	private CyclicBarrier barrier = null;
	private long succTaskCount = 0;
	
	private AckMessageCache(){};
	
	public long getSuccTaskCount(){
		return succTaskCount;
	}
	
	private static class  AckMessageCacheHolder{
		public static AckMessageCache instance = new AckMessageCache();
	}
	
	public static AckMessageCache getInsance(){
		return AckMessageCacheHolder.instance;
	}
	
	
	public void parallelDispatch(LinkedList<String> list){
		List<Callable<Long>> tasks = new ArrayList<>();
		List<Future<Long>> futureList = new ArrayList<Future<Long>>();
        int startPosition = 0;
        Pair<Integer, Integer> pair = calculateBlocks(list.size(), list.size());
        int numberOfThreads = pair.getRight();
        int blocks = pair.getLeft();
        
        barrier = new CyclicBarrier(numberOfThreads);
        
        for (int i = 0; i < numberOfThreads; i++) {
            String[] task = new String[blocks];
            System.arraycopy(list.toArray(), startPosition, task, 0, blocks);
            tasks.add(new AckMessageTask(barrier, task));
            startPosition += blocks;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        try {
			futureList = executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        

	}
}
