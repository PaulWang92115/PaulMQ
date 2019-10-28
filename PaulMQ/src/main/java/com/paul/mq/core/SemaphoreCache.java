package com.paul.mq.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class SemaphoreCache {
	
    private static int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
	
	// loadCache 与 concurrentHashMap 的作用类似，但是loadingCache 能定义更多的元素失效策略。还可以监控缓存加载命中情况
	private static final LoadingCache<String, Semaphore> cache = CacheBuilder.newBuilder().
			concurrencyLevel(workerThreads).build(new CacheLoader<String, Semaphore>(){
				@Override
				public Semaphore load(String key) throws Exception {
					return new Semaphore(0);
				}
			});
	
	public static int getAvailablePermits(String key){
		try {
			return cache.get(key).availablePermits();
		} catch (ExecutionException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static void release(String key){
		try {
			cache.get(key).release();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void acquire(String key){
		try {
			cache.get(key).acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
