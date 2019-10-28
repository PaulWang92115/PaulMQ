package com.paul.mq.util;

import io.netty.channel.Channel;

import com.google.common.base.Preconditions;

public class NettyUtil {
    public static boolean validateChannel(Channel channel) {
        Preconditions.checkNotNull(channel, "channel can not be null");
        return channel.isActive() && channel.isOpen() && channel.isWritable();
    }
}
