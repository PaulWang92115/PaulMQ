package com.paul.mq.common;

/*
 * CcallBack 将 netty 的异步调用变为同步，CallBackListener 是 CallBack
 * 里面的回调接口
 */
public interface CallBackListener<T> {
	
    void onCallBack(T t);

}
