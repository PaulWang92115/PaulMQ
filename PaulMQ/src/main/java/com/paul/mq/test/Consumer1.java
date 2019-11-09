package com.paul.mq.test;

import com.paul.mq.consumer.PaulMQConsumer;
import com.paul.mq.consumer.ReceiveMessageCallBack;
import com.paul.mq.entity.Exchange;
import com.paul.mq.entity.Message;

public class Consumer1 {
	public static ReceiveMessageCallBack callBack = new ReceiveMessageCallBack(){
		@Override
		public void onCallBack(Message message) {
			System.out.printf("PaulMQConsumer 收到消息编号:%s,消息内容:%s\n", message.getMsgId(), new String(message.getBody()));
		}
	};
	
	public static void main(String[] args){
		Exchange exchange = new Exchange();
		exchange.setName("Exchanger");
		exchange.setRegrex(".*update.*");
		PaulMQConsumer consumer = new PaulMQConsumer("0.0.0.0",8092,"",exchange,callBack);
		consumer.init();
		consumer.start();
	}
}
