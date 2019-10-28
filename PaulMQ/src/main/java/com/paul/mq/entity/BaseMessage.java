package com.paul.mq.entity;

import java.io.Serializable;
/**
 * netty 客户端与服务端之间消息传递的实体类
 *
 */
public class BaseMessage implements Serializable {

	private static final long serialVersionUID = 1545712808556462228L;
	
	//消息 id
	private String msgId;
	// 消息类
    private AbstractMessage message;
    // 消息类型
    private MessageType messageType;
    // 发送消息的类型（producer/broker/consumer）
    private SourceType sourceType;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    
    public AbstractMessage getMessage() {
		return message;
	}

	public void setMessage(AbstractMessage message) {
		this.message = message;
	}

	public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
}
