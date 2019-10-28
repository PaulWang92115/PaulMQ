package com.paul.mq.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public abstract class CallBack<T> {

    //CallBack 的 listener 的集合
    public List<CallBackListener<T>> listeners = Collections.synchronizedList(new ArrayList<CallBackListener<T>>());

    // netty 通信正常返回结果信息
    public T messageResult;
    // request Id
    public String requestId;
    // netty 通信发生异常
    public Throwable reason;
    
    
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public abstract void setReason(Throwable reason);

    public abstract void setMessageResult(T messageResult);

    public abstract T getMessageResult(long timeout, TimeUnit unit);


    public void addListeners(CallBackListener<T> listener){
        this.listeners.add(listener);
    }
    public void onListeners(){
        for(CallBackListener<T> listener:listeners){
            listener.onCallBack(messageResult);
        }
    }
}
