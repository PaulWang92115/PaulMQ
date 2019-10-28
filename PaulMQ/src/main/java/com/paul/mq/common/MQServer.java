package com.paul.mq.common;


/**
 * * MQServer 模版，对于所有服务端或者连接服务端的类都需要继承这个方法
 * * init 初始化连接服务
 * * start 开始连接
 * * stop 关闭连接
 *
 */
public interface MQServer {
	
    void init();

    void start();

    void stop();
}
