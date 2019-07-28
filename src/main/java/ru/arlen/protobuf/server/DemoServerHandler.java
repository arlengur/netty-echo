package ru.arlen.protobuf.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.arlen.server.proto.DemoProtocol.DemoRequest;
import ru.arlen.server.proto.DemoProtocol.DemoResponse;

public class DemoServerHandler extends SimpleChannelInboundHandler<DemoRequest> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DemoRequest msg) throws Exception {
    System.out.println("Got request msg from Client: " + msg.getRequestMsg());
    DemoResponse.Builder builder = DemoResponse.newBuilder();
    builder.setResponseMsg("Accepted from Server, returning response").setRet(0);
    ctx.write(builder.build());
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // cause.printStackTrace();
    ctx.close();
  }

}
