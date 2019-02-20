package cn.netty.server.core;

import cn.netty.server.handler.GlobleHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private GlobleHandler globleHandler;

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec())
            .addLast(new HttpObjectAggregator(65536))
            .addLast(new ChunkedWriteHandler())
            .addLast(globleHandler);
    }
}
