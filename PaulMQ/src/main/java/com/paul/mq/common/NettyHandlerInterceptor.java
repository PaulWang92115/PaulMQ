package com.paul.mq.common;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;




public class NettyHandlerInterceptor implements MethodInterceptor{

	private MessageProxy proxy;
	private Object msg;
	

	public NettyHandlerInterceptor(MessageProxy proxy, Object msg) {
		super();
		this.proxy = proxy;
		this.msg = msg;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		proxy.beforeHandle(msg);
		Object result = invocation.proceed();
		proxy.afterHandle(msg);
		return result;
	}


}
