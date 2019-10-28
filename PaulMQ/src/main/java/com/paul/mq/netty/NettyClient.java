package com.paul.mq.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paul.mq.common.CallBack;
import com.paul.mq.common.MQServer;
import com.paul.mq.serialize.KryoCodecUtil;
import com.paul.mq.serialize.KryoPoolFactory;
import com.paul.mq.serialize.MessageObjectDecoder;
import com.paul.mq.serialize.MessageObjectEncoder;
/**
 * netty 客户端，producer 和 consumer 都需要使用这个类连接 netty 服务端
 * @author swang18
 *
 */
public class NettyClient implements MQServer {

    //服务端地址
    private SocketAddress remoteAddr;
    //客户端的 netty handler，外部传入
    private ChannelInboundHandlerAdapter clientHandler;
    //连接成功后的 channel
    private Channel channel;
    //是否连接成功
    private boolean connected = false;
    //发送消息的超时时间
    private int timeout = 10*1000;

    //每个请求的回调，通过这个保存所有请求，CallBack 将 netty 的异步调用变为同步
    Map<String,CallBack<Object>> callBackMap = new ConcurrentHashMap<String, CallBack<Object>>();
    
    private static KryoCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());


    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public NettyClient(String host,Integer port){
        remoteAddr = new InetSocketAddress(host,port);
    }


    //初始化 netty 连接配置
    public void init() {

        //功能描述：检查boolean是否为真。 用作方法中检查参数
        //失败时抛出的异常类型: IllegalArgumentException
    	if(null == clientHandler){
    		throw new RuntimeException("Client Handler is Null!");
    	}

        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();

        try{
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new MessageObjectEncoder(util));
                            pipeline.addLast(new MessageObjectDecoder(util));
                            pipeline.addLast(clientHandler);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // 客户端（生产者/消费者）连接服务端 broker server
    public void start() {
    	init();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(this.remoteAddr).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                channel = future.channel();
            }
        });

        System.out.println("connect broker server succeess,borker server ip address:" + this.remoteAddr.toString());
        connected = true;
    }

    public void stop() {
        if(null != channel){
            try {
                //关闭 channel
                channel.close().sync();
                eventLoopGroup.shutdownGracefully();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //返回连接成功后的 channel
    public Channel getChannel(){
        return channel;
    }

    // 返回是否连接成功
    public boolean isConnected(){
        return connected;
    }

    // 返回当前 channel 的所有 request 的 callbackMap 集合
    public Map<String,CallBack<Object>> getCallBackMap(){
        return callBackMap;
    }
    
    public boolean checkCallBack(String key){
    	return callBackMap.containsKey(key);
    }

    public CallBack<Object> removeCallBack(String key){
        if(null == key){
            return null;
        }else{
            if(callBackMap.containsKey(key)){
                return callBackMap.remove(key);
            }else{
                return null;
            }
        }
    }

    public void setMessageHandle(ChannelInboundHandlerAdapter clientHandler) {
        this.clientHandler = clientHandler;
    }

    public int getTimeout() {
        return timeout;
    }
}
