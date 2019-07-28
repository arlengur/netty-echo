package ru.arlen.protobuf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
// import io.netty.handler.logging.LogLevel;
// import io.netty.handler.logging.LoggingHandler;

public class DemoServer {

  static final int PORT = Integer.parseInt(System.getProperty("port", "8000"));

  public static void main(String[] args) throws InterruptedException {
    
    // Create event loop groups. One for incoming connections handling and 
    // second for handling actual event by workers
    EventLoopGroup serverGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    
    try {
      ServerBootstrap bootStrap = new ServerBootstrap();
      bootStrap.group(serverGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        // .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new DemoServerInitializer());

      // Bind and start to accept incoming connections.
      ChannelFuture f = bootStrap.bind(PORT).sync();
      System.out.println("Starting nio server at " + f.channel().localAddress());

      // Wait until server socket is closed
      f.channel().closeFuture().sync();
    } finally {
      serverGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
