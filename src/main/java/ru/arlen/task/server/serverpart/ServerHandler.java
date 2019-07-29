package ru.arlen.task.server.serverpart;

import static ru.arlen.task.server.utils.Utils.ONE_MIN;
import static ru.arlen.task.server.utils.Utils.agregateTask;
import static ru.arlen.task.server.utils.Utils.getDateMillis;
import static ru.arlen.task.server.utils.Utils.getDateStr;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.InvalidProtocolBufferException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import ru.arlen.task.proto.TaskProtocol.TaskRequest;
import ru.arlen.task.server.core.InMemoryStore;

public class ServerHandler extends SimpleChannelInboundHandler<TaskRequest> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final InMemoryStore store;
  private ScheduledFuture<?> sf;

  public ServerHandler(InMemoryStore store) {
    this.store = store;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    long now = System.currentTimeMillis();
    long initDelay = getDateMillis(getDateStr(now + ONE_MIN)) - now;
    sf = ctx.executor().scheduleAtFixedRate(() -> {
      try {
        agregateTask(store.getOneMTrades(), 1, System.currentTimeMillis()).stream()
            .forEach(task -> ctx.writeAndFlush(task));
      } catch (InvalidProtocolBufferException e) {
        logger.error("UInvalidProtocolBufferException: {}", e.getMessage());
      }
    }, initDelay, ONE_MIN, TimeUnit.MILLISECONDS);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    sf.cancel(true);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TaskRequest msg) throws Exception {
    agregateTask(store.getTenMTrades(), 10, System.currentTimeMillis()).stream()
        .forEach(task -> ctx.write(task));
    logger.debug("User connected: {}", ctx.channel().remoteAddress());
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.close();
  }
}
