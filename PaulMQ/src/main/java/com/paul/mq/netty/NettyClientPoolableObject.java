package com.paul.mq.netty;

import org.apache.commons.pool.PoolableObjectFactory;

/**
 * nettyclient 对象池的工厂方法，用于创建和销毁 nettyclient
 * @author swang18
 *
 */
public class NettyClientPoolableObject implements PoolableObjectFactory<NettyClient> {

    private String host;
    private Integer port;

    public NettyClientPoolableObject(String host,Integer port){
        this.host = host;
        this.port = port;
    }


    public NettyClient makeObject() throws Exception {
        NettyClient client = new NettyClient(host,port);
        return client;
    }

    //对象销毁就直接调用 nettyClient 的stop 方法销毁 channel
    public void destroyObject(NettyClient nettyClient) throws Exception {
        if(nettyClient instanceof NettyClient){
            nettyClient.stop();
        }
    }

    public boolean validateObject(NettyClient nettyClient) {
        return true;
    }

    public void activateObject(NettyClient nettyClient) throws Exception {

    }

    public void passivateObject(NettyClient nettyClient) throws Exception {

    }
}

