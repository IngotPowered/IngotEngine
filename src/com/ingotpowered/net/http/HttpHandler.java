package com.ingotpowered.net.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public abstract class HttpHandler {

    public void onError(Channel channel, Throwable cause) {  }

    public void onSuccess(ChannelHandlerContext context, String data) { }
}
