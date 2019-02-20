package cn.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface Handler {
    void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request);
}
