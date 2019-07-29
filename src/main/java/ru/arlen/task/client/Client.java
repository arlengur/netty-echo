package ru.arlen.task.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

  static final String HOST = System.getProperty("host", "127.0.0.1");
  static final int PORT = Integer.parseInt(System.getProperty("port", "8000"));

  public static void main(String[] args) throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group).channel(NioSocketChannel.class).handler(new ClientInitializer());

      // Create connection
      Channel ch = bootstrap.connect(HOST, PORT).sync().channel();

      // Get handle to handler so we can send message
      ClientHandler handle = ch.pipeline().get(ClientHandler.class);
      handle.sendRequest();

      ch.closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }

  }
}
