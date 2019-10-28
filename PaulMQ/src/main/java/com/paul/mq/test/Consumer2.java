package com.paul.mq.test;

import com.paul.mq.consumer.PaulMQConsumer;
import com.paul.mq.consumer.ReceiveMessageCallBack;
import com.paul.mq.entity.Message;

public class Consumer2 {
	public static ReceiveMessageCallBack callBack = new ReceiveMessageCallBack(){
		@Override
		public void onCallBack(Message message) {
			System.out.printf("PaulMQConsumer 收到消息编号:%s,消息内容:%s\n", message.getMsgId(), new String(message.getBody()));
		}
	};
	
	public static void main(String[] args){
		PaulMQConsumer consumer = new PaulMQConsumer("0.0.0.0",8092,"MQCluster2","Topic-1",callBack);
		consumer.init();
		consumer.start();
	}
}
