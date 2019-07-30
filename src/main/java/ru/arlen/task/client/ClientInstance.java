package ru.arlen.task.client;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * ClientInstance
 */
public class ClientInstance extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String host;
    private final int port;

    public ClientInstance(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).handler(new ClientInitializer());

            // Create connection
            Channel ch = bootstrap.connect(host, port).sync().channel();

            // Get handle to handler so we can send message
            ClientHandler handle = ch.pipeline().get(ClientHandler.class);
            handle.sendRequest();

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            // time to stop
        } finally {
            group.shutdownGracefully();
        }
        logger.info("Client Stopped.");
    }
}