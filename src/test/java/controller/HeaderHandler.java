package controller;

import cn.netty.server.core.RequestMapping;
import cn.netty.server.handler.BaseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequestMapping("/header")
public class HeaderHandler extends BaseHandler {

    @Override
    public void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : request.headers()) {
            sb.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
        }
        renderStr(ctx, sb.toString());
    }
}
