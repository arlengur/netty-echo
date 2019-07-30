package ru.arlen.task.server.serverpart;

import static ru.arlen.task.server.utils.Constants.ONE_MINUTE;
import static ru.arlen.task.server.utils.Utils.agregateOneMinTasks;
import static ru.arlen.task.server.utils.Utils.agregateTenMinTasks;
import static ru.arlen.task.server.utils.Utils.getNoSecMillis;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import ru.arlen.task.proto.TaskProtocol.TaskRequest;
import ru.arlen.task.server.core.Persistent;

public class ServerHandler extends SimpleChannelInboundHandler<TaskRequest> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final Persistent store;
  private ScheduledFuture<?> sf;

  public ServerHandler(Persistent store) {
    this.store = store;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    long now = System.currentTimeMillis();
    long initDelay = getNoSecMillis(now + ONE_MINUTE) - now;
    sf = ctx.executor().scheduleAtFixedRate(() -> {
      agregateOneMinTasks(store.getOneMinT()).stream().forEach(task -> ctx.writeAndFlush(task));
    }, initDelay, ONE_MINUTE, TimeUnit.MILLISECONDS);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    sf.cancel(true);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TaskRequest msg) throws Exception {
    agregateTenMinTasks(store.getTenMinT()).stream().forEach(task -> ctx.write(task));
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
