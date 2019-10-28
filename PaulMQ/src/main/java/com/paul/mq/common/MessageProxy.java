package com.paul.mq.common;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * handler message 方法的代理，可以在方法执行之前和执行之后做一些自定义的操作
 *
 */
public interface MessageProxy {
	
	//因为是对 netty 的消息进行处理，所以参数中会传入 netty 传过来的消息
	void beforeHandle(Object msg);
	
	void afterHandle(Object msg);


}
