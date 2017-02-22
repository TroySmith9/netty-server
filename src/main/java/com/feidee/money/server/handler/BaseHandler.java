package com.feidee.money.server.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by tingsky on 16/6/14.
 */
public abstract class BaseHandler implements Handler{
    private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);

    public static String getBody(FullHttpRequest request){
        String body = request.content().toString(Charset.forName("UTF-8"));
        log.debug("Recived Request body :{}", body);
        return body;
    }

    public static void renderStr(ChannelHandlerContext ctx, String str){
        render(ctx, "text/plain", str);
    }

    public static void renderStr(ChannelHandlerContext ctx, HttpResponseStatus status, String str){
        render(ctx, status, "text/plain", str);
    }

    public static void render(ChannelHandlerContext ctx, String contentType, String str){
        render(ctx, HttpResponseStatus.OK, contentType, str);
    }

    public static void render(ChannelHandlerContext ctx, HttpResponseStatus status, String contentType, String str){
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, contentType + "; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void renderJson(ChannelHandlerContext ctx, HttpResponseStatus status, JSONObject json){
        render(ctx, status, "application/json", json.toString());
    }

    public static void renderJson(ChannelHandlerContext ctx, JSONObject json){
        render(ctx, HttpResponseStatus.OK, "application/json", json.toString());
    }

    public static void renderError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        renderStr(ctx, status, "Failure: " + status);
    }
}
