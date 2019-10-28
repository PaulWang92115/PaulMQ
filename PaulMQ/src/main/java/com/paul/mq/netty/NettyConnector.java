package com.paul.mq.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.paul.mq.common.CallBack;
import com.paul.mq.common.CountDownCallBack;
import com.paul.mq.entity.RequestMessage;


/**
*
* 通过管理 NettyClientPool 来获取 netty 连接，并且定义向 broker server 发送消息的方法
* 生产者，消费者都需要继承这个类
*
*/
public class NettyConnector {
	
    private NettyClient nettyClient;
    private NettyClientPool nettyClientPool;
    
    public NettyConnector(String host,Integer port){
        NettyClientPool.setServerAddress(host,port);
        this.nettyClientPool = NettyClientPool.getInstance();
        this.nettyClient = nettyClientPool.borrow();
    }
    
    public NettyClient getNettyClient(){
        return nettyClient;
    }

    public void closeNettyClientPool(){
        nettyClientPool.restore();
    }
    
    //同步方式发送消息
    public void sendSyncMessage(RequestMessage message){
        Channel channel = nettyClient.getChannel();
        if(null == channel){
            return;
        }
        Map<String,CallBack<Object>> callBackMap = nettyClient.getCallBackMap();
        //将当前这个 request 也放到这个 channel 对应的 callbackMap 中
        final CallBack<Object> callBack = new CountDownCallBack<Object>();
        callBack.setRequestId(message.getMsgId());
        callBackMap.put(message.getMsgId(),callBack);

        try {
            ChannelFuture channelFuture = channel.writeAndFlush(message).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()){
                        callBack.setReason(channelFuture.cause());
                    }
                }
            });
            //因为这个方法是 void ，所以不需要 setMessageResult了，只需要在异常的时候 setReason 就可以了。

        } catch (InterruptedException e) {
            Logger.getLogger(NettyConnector.class.getName()).log(Level.SEVERE, null, e);
        }


    }

    //异步方式发送消息
    public Object sendAsyncMessage(RequestMessage message){
        Channel channel = nettyClient.getChannel();
        if(null == channel){
            return null;
        }
        Map<String,CallBack<Object>> callBackMap = nettyClient.getCallBackMap();
        //将当前这个 request 也放到这个 channel 对应的 callbackMap 中
        final CallBack<Object> callBack = new CountDownCallBack<Object>();
        callBack.setRequestId(message.getMsgId());
        callBackMap.put(message.getMsgId(),callBack);

        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(!channelFuture.isSuccess()){
                    callBack.setReason(channelFuture.cause());
                }
            }
        });
        // setMessageResult 函数不在这里设置
        // 异步操作，通过加锁的方式获取 messageResult
        Object result = callBack.getMessageResult(nettyClient.getTimeout(), TimeUnit.MILLISECONDS);
        callBackMap.remove(message.getMsgId());
        return result;
    }

}
