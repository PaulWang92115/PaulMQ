package com.paul.mq.common;

import io.netty.channel.ChannelHandlerContext;

public interface MessageProcessor {
	void handle(ChannelHandlerContext ctx, Object msg);

}
