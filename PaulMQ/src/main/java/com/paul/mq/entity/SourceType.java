package com.paul.mq.entity;

/***
 *
 * sourceType, 用来指定发送消息的是生产者，消费者，还是 broker server。
 *
 */
public enum SourceType {

    PRODUCER(1),
    CONSUMER(2),
    BROKER(3);

    private int source;

    private SourceType(int source){
        this.source = source;
    }

    public int getSource() {
        return source;
    }

}
