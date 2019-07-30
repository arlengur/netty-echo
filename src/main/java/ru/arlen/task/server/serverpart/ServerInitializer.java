package ru.arlen.task.server.serverpart;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.arlen.task.proto.TaskProtocol.TaskRequest;
import ru.arlen.task.server.core.Persistent;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
  private final Persistent store;
  private final EventExecutorGroup executers;

  public ServerInitializer(EventExecutorGroup executers, Persistent store) {
    this.store = store;
    this.executers = executers;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline()
        .addLast(new ProtobufVarint32FrameDecoder())
        .addLast(new ProtobufDecoder(TaskRequest.getDefaultInstance()))
        .addLast(new ProtobufVarint32LengthFieldPrepender())
        .addLast(new ProtobufEncoder())
        .addLast(executers, new ServerHandler(store));
  }
}
