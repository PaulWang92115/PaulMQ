package com.paul.mq.test;

import com.paul.mq.entity.Message;
import com.paul.mq.entity.ProducerAckMessage;
import com.paul.mq.producer.PaulMQProducer;


public class Producer1 {
	
	public static void main(String[] args){
		PaulMQProducer producer = new PaulMQProducer("127.0.0.1",8092,"Topic-1");
        producer.init();
        producer.start();
        
        System.out.println("开始发送数据");
        
        Message message = new Message();
        String str = "Hello PaulMQ From Producer1[" + 1 + "]";
        message.setBody(str.getBytes());
        ProducerAckMessage result = producer.produce(message);
        if (result.getStatus() == (ProducerAckMessage.SUCCESS)) {
            System.out.printf("PaulMQProducer1 生产消息发送成功得到反馈\n", result.getMsgId());
        }
        
        System.out.println("发送数据结束");
        producer.stop();
	}
}
