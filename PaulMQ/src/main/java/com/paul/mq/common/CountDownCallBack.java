package com.paul.mq.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownCallBack<T> extends CallBack<T>{

    private final CountDownLatch countDownLatch = new CountDownLatch(1);


    public void setReason(Throwable reason) {
        this.reason = reason;
        onListeners();
        countDownLatch.countDown();
    }

    public void setMessageResult(T messageResult) {
        this.messageResult = messageResult;
        onListeners();
        countDownLatch.countDown();
    }

    public T getMessageResult(long timeout, TimeUnit unit) {
        try {
            countDownLatch.await(timeout,unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //发生异常，直接返回 null
        if(reason!=null){
            return null;
        }
        //如果拿countdownlatch 的锁超时，则会返回null，否则正常返回结果
        return messageResult;
    }
}
