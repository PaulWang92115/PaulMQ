package com.paul.mq.broker;

import com.paul.mq.common.CallBack;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class LauncherListener implements ChannelFutureListener{
	
	private CallBack<Object> invoke = null;
	
	public LauncherListener(CallBack<Object> invoke){
		this.invoke = invoke;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if(!future.isSuccess()){
			invoke.setReason(future.cause());
		}
	}

}
