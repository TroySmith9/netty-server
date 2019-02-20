package cn.netty.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class NettyContainer implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(NettyContainer.class);

    @Value("#{cfg['netty.port']}")
    private int port;

    @Value("#{cfg['uri.prefix']}")
    private String uriPrefix = "";

    @Autowired
    private ServerInitializer serverInitializer;

    private static Thread t;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                startNettyServer();
            }
        }, "NettyThread");
        t.start();
    }

    private void startNettyServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(serverInitializer);

            Channel ch = b.bind(port).sync().channel();

            log.info("Open your web browser and navigate to  http://127.0.0.1:" + port + uriPrefix +'/');

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Netty启动过程出错", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
