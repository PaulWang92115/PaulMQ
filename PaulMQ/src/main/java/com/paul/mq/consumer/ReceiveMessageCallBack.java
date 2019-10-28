package com.paul.mq.consumer;

import com.paul.mq.entity.AbstractMessage;
import com.paul.mq.entity.ConsumerAckMessage;
import com.paul.mq.entity.Message;


/**
 * 
 * consumer 接受到消息后的回调方法，使用者自己实现，自己对接收到的消息结合业务逻辑进行处理
 *
 */
public interface ReceiveMessageCallBack {
	void onCallBack(Message paramMessage);
}
