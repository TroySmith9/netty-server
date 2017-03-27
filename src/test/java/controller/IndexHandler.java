package controller;

import com.alibaba.fastjson.JSONObject;
import cn.netty.server.core.RequestMapping;
import cn.netty.server.handler.BaseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * Created by tingsky on 16/5/22.
 */
@Controller
@RequestMapping("/")
public class IndexHandler extends BaseHandler{

    private static final Logger log = LoggerFactory.getLogger(IndexHandler.class);

    @Override
    public void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        JSONObject obj = new JSONObject();
        obj.put("hello", "world");
        renderJson(ctx, HttpResponseStatus.OK, obj);
        log.info("ok");
    }
}
