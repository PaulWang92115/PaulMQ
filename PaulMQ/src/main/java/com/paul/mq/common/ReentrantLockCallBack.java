package com.paul.mq.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ReentrantLockCallBack<T> extends CallBack<T>{

    private final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();


    public void setReason(Throwable reason) {
        this.reason = reason;
        onListeners();
        try{
            lock.lock();
            condition.signal();
        }finally {
            lock.unlock();
        }

    }

    public void setMessageResult(T messageResult) {
        this.messageResult = messageResult;
        onListeners();
        try{
            lock.lock();
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    public T getMessageResult(long timeout, TimeUnit unit) {
        try{
            lock.lock();
            try {
                condition.await(timeout,unit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(reason!=null){
                return null;
            }
            return messageResult;
        }finally {
            lock.unlock();
        }
    }
}

