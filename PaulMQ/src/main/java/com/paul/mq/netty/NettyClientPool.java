package com.paul.mq.netty;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.pool.impl.GenericObjectPool;


/**
 * 通过 common pool 的 GenericObjectPool 构建 nettyClient 的对象池，当然也可以叫连接池。
 * 连接池使用单例模式就可以
 */

public class NettyClientPool extends GenericObjectPool<NettyClient> {

    private static volatile NettyClientPool nettyClientPool;

    private static String configProperties = "com/paul/mq/netty/mq.connectpool.properties";

    // 服务端地址 ip 和端口
    private static String host;
    private static Integer port;


    public static NettyClientPool getInstance(){
        if(nettyClientPool == null){
            synchronized (NettyClientPool.class){
                if(nettyClientPool == null){
                    nettyClientPool = new NettyClientPool();
                }
            }
        }
        return nettyClientPool;
    }

    private NettyClientPool(){
    	Properties properties = null;
        try {
        	properties = new Properties();
            InputStream inputStream = NettyClientPool.class.getClassLoader().getResourceAsStream(configProperties);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int maxActive = Integer.parseInt(properties.getProperty("maxActive"));
        int minIdle = Integer.parseInt(properties.getProperty("minIdle"));
        int maxIdle = Integer.parseInt(properties.getProperty("maxIdle"));
        int maxWait = Integer.parseInt(properties.getProperty("maxWait"));
        int sessionTimeOut = Integer.parseInt(properties.getProperty("sessionTimeOut"));

        System.out.printf("NettyClientPool[maxActive=%d,minIdle=%d,maxIdle=%d,maxWait=%d,sessionTimeOut=%d]\n", maxActive, minIdle, maxIdle, maxWait, sessionTimeOut);
        this.setMaxActive(maxActive);
        this.setMaxIdle(maxIdle);
        this.setMinIdle(minIdle);
        this.setMaxWait(maxWait);
        this.setTestOnBorrow(false);
        this.setTestOnReturn(false);
        this.setTimeBetweenEvictionRunsMillis(10 * 1000);
        this.setNumTestsPerEvictionRun(maxActive + maxIdle);
        this.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        this.setTestWhileIdle(true);
        // 虽然这个函数已经废除，因为我们是单例，不能使用构造函数传入，所以还是使用这个方法传入 PoolableObjectFactory
        this.setFactory(new NettyClientPoolableObject(host,port));
    }

    public NettyClient borrow(){
        assert nettyClientPool != null;
        try {
            return nettyClientPool.borrowObject();
        } catch (Exception e) {
            System.out.printf("get netty connection throw the error from netty connection pool, error message is %s\n",
                    e.getMessage());
        }
        return null;
    }

    public void restore(){
        assert nettyClientPool != null;
        try {
			nettyClientPool.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    //单例模式，通过 set 方法给 NettyClient 必要的函数 host 和 port 赋值
    public static void setServerAddress(String nhost,Integer nport) {
    	host = nhost;
    	port = nport;
    }
}

