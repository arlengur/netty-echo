package ru.arlen.task.client;

import com.google.protobuf.util.JsonFormat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.arlen.task.proto.TaskProtocol.TaskRequest;
import ru.arlen.task.proto.TaskProtocol.TaskResponse;

public class ClientHandler extends SimpleChannelInboundHandler<TaskResponse> {
    private Channel channel;

    public void sendRequest() {
        TaskRequest req = TaskRequest.newBuilder().build();
        channel.writeAndFlush(req);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskResponse msg) throws Exception {
        System.out.println(JsonFormat.printer().omittingInsignificantWhitespace().print(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
