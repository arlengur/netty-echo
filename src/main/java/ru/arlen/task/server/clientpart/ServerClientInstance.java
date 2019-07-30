package ru.arlen.task.server.clientpart;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.arlen.task.server.core.InMemoryStore;

/**
 * ServerClientInstance
 */
public class ServerClientInstance extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final InMemoryStore store;
    private final String host;
    private final int port;

    public ServerClientInstance(String host, int port, InMemoryStore store) {
        this.host = host;
        this.port = port;
        this.store = store;
    }

    public void run() {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        EventExecutorGroup executers = new DefaultEventExecutorGroup(4);
        try {
            Bootstrap client = new Bootstrap();
            client.group(clientGroup).channel(NioSocketChannel.class)
                    .handler(new ServerClientInitializer(executers, store));
            // Create client connection
            client.connect(host, port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // time to stop
        } finally {
            executers.shutdownGracefully();
            clientGroup.shutdownGracefully();
        }
        logger.info("Server Client Stopped.");
    }
}