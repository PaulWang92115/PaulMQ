package com.paul.mq.broker;

import java.util.concurrent.Callable;

import com.paul.mq.core.AckMessageCache;

public class AckPushMessageController implements Callable<Void>{

    private volatile boolean stoped = false;
	
    public void stop() {
        stoped = true;
    }

    public boolean isStoped() {
        return stoped;
    }
    
	@Override
	public Void call() throws Exception {
		AckMessageCache cache = AckMessageCache.getInsance();
		int timeout  = 1000;
		while(!stoped){
			if(cache.hold(timeout)){
				cache.commit();
			}
		}
		return null;
	}

}
