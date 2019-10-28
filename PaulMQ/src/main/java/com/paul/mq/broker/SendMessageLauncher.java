package com.paul.mq.broker;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import com.paul.mq.common.CallBack;
import com.paul.mq.common.CountDownCallBack;
import com.paul.mq.entity.ResponseMessage;

public class SendMessageLauncher {
	
	private int timeout = 3000;
	//ConcurrentSkipListMap 需要研究一下
	public Map<String,CallBack<Object>> invokeMap = new ConcurrentSkipListMap<String,CallBack<Object>>();
	
	private SendMessageLauncher(){};
	
	private volatile static SendMessageLauncher instance;
	
	public static SendMessageLauncher getInstance(){
		if(null == instance){
			synchronized(SendMessageLauncher.class){
				if(null == instance){
					instance = new SendMessageLauncher();
				}
			}
		}
		return instance;
	}
	
	public Object launcher(Channel channel, ResponseMessage response){
		if(null != channel){
			CallBack<Object> invoke = new CountDownCallBack<Object>();
			invokeMap.put(response.getMsgId(), invoke);
			invoke.setRequestId(response.getMsgId());
			ChannelFuture future = channel.writeAndFlush(response);
			future.addListener(new LauncherListener(invoke));
			try{
				Object result = invoke.getMessageResult(timeout, TimeUnit.MILLISECONDS);
				return result;
			}finally{
				invokeMap.remove(response.getMsgId());
			}
		}else{
			return null;
		}
	}
	
    public boolean trace(String key) {
        return invokeMap.containsKey(key);
    }

    public CallBack<Object> detach(String key) {
        if (invokeMap.containsKey(key)) {
            return invokeMap.remove(key);
        } else {
            return null;
        }
    }
}
