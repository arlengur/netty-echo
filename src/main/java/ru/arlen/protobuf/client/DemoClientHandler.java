package ru.arlen.protobuf.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.arlen.server.proto.DemoProtocol.DemoRequest;
import ru.arlen.server.proto.DemoProtocol.DemoResponse;

public class DemoClientHandler extends SimpleChannelInboundHandler<DemoResponse> {
    private Channel channel;

    public void sendRequest() {
        DemoRequest req = DemoRequest.newBuilder().setRequestMsg("From Client").build();
        channel.writeAndFlush(req);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DemoResponse msg) throws Exception {
        System.out.println("Got reponse msg from Server: " + msg.getResponseMsg());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // cause.printStackTrace();
        ctx.close();
    }
}
