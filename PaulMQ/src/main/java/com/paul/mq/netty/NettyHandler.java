package com.paul.mq.netty;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import com.paul.mq.common.MessageProcessor;
import com.paul.mq.common.MessageProxy;
import com.paul.mq.common.NettyHandlerInterceptor;
import com.paul.mq.consumer.ReceiveMessageCallBack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * netty 连接的 handler 的模板类，所有 handler 都需要继承这个类，并且实现 before，handle，after 方法来处理业务逻辑
 *
 * @param <T>
 */
public class NettyHandler<T> extends SimpleChannelInboundHandler<T> implements MessageProcessor,MessageProxy{
	
    public final static String proxyMappedName = "handle";
	//handler 是针对 nettyclient的，所以我们需要 nettyclient 的引用，如果需要拿 nettyclient 的引用，
	// 我们就需要 NettyConnector
	// 子类需要继承这些属性，所以设置为 protected
	protected NettyConnector connector;
	protected NettyClient nettyClient;
	
    // 这个引用是想让子类把自己的实例传入，好让 handleMessage 方法做代理
    protected NettyHandler<T> subHandler;
    
    //consumer 接收到消息的回调方法，producer 不需要实现
    protected ReceiveMessageCallBack callBack;
    
    //netty 传输层面的异常
    protected Throwable cause;
    
    //服务端 handler 使用的构造方法，不用传参数
	public NettyHandler(){
	}
    
	//客户端构造需要传 connector
	public NettyHandler(NettyConnector connector){
		this(connector, null);
	}
    
	public NettyHandler(NettyConnector connector,ReceiveMessageCallBack callBack){
		this.connector = connector;
		this.nettyClient = connector.getNettyClient();
		this.callBack = callBack;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, T msg)
			throws Exception {
		//netty 读到了消息，要将这个消息转给它实现的子类去实现
		ProxyFactory proxyFactory = new ProxyFactory(subHandler);
		// 根据方法名做切面
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.addMethodName(proxyMappedName);
        //静态代理，保证 before handle after 的方法执行顺序
        advisor.setAdvice(new NettyHandlerInterceptor(subHandler,msg));
        proxyFactory.addAdvisor(advisor);
        
        //执行子类的 handler 方法
        MessageProcessor processor = (MessageProcessor)proxyFactory.getProxy();
        processor.handle(ctx,msg);
	}
	
	

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		this.cause = cause;
	}
	
	public void setSubHandler(NettyHandler<T> subHandler) {
		this.subHandler = subHandler;
	}

	public Throwable getCause() {
		return cause;
	}

	//下面三个函数是留给子类去实现的
	public void handle(ChannelHandlerContext ctx, Object msg) {

	}

	public void beforeHandle(Object msg) {
		
	}

	public void afterHandle(Object msg) {
		
	}


	


}
