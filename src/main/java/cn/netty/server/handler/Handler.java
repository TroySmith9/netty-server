package cn.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by tingsky on 16/5/23.
 */
public interface Handler {
    void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request);
}
