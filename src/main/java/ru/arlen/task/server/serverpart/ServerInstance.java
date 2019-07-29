package ru.arlen.task.server.serverpart;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.arlen.task.server.core.InMemoryStore;

/**
 * ServerInstance
 */
public class ServerInstance extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final InMemoryStore store;
    private final int port;

    public ServerInstance(int port, InMemoryStore store) {
        this.port = port;
        this.store = store;
    }

    public void run() {
        EventLoopGroup serverGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventExecutorGroup executers = new DefaultEventExecutorGroup(4);
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(serverGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer(executers, store));

            // Bind and start to accept incoming connections.
            ChannelFuture f = server.bind(port).sync();
            System.out.println("Starting nio server at " + f.channel().localAddress());
            // Wait until server socket is closed
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // time to stop
        } finally {
            serverGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            executers.shutdownGracefully();
        }
        logger.info("Server Stopped.");
    }
}