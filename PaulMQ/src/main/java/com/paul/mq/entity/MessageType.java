package com.paul.mq.entity;

public enum MessageType {

    REGISTER(1),     //消费端订阅 topic
    UNREGISTER(2),   //消费端取消订阅 topic
    MESSAGE(3),       //生产者，消费者发送消息给 broker
    PRODUCERACK(4),   //生产者发送消息后得到的应答
    CONSUMERACK(5);   //消费者收到消息后返回的应答

    private int type;

    private MessageType(int type){
        this.type = type;
    }

    public int getMessageType() {
        return type;
    }

}
