package com.paul.mq.core;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MessageCache<T>{
	private ConcurrentLinkedQueue<T> cache = new ConcurrentLinkedQueue<T>();
	private Semaphore semaphore = new Semaphore(0);
	
	public void appendMessage(T id){
		cache.add(id);
		semaphore.release();
	}
	
	public void parallelDispatch(LinkedList<T> list){
		
	}
	
	public void commit(ConcurrentLinkedQueue<T> tasks){
		commitMessage(tasks);
	}
	
	public void commit(){
		commitMessage(cache);
	}
	
	private void commitMessage(ConcurrentLinkedQueue<T> messages){
		LinkedList<T> list = new LinkedList<T>();
		list.addAll(messages);
		messages.clear();
		
		if(list != null&&list.size()>0){
			parallelDispatch(list);
			list.clear();
		}
	}
	
	public boolean hold(long timeout){
		try {
			return semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected Pair<Integer,Integer> calculateBlocks(int parallel,int sizeOfTasks){
		int numberOfThreads = parallel > sizeOfTasks ? sizeOfTasks : parallel;
		Pair<Integer,Integer> pair = new MutablePair<Integer,Integer>(new Integer(sizeOfTasks/numberOfThreads), new Integer(numberOfThreads));
		return pair;
	}
}
