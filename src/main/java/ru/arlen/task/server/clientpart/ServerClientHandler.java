package ru.arlen.task.server.clientpart;

import static io.netty.util.CharsetUtil.US_ASCII;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.arlen.task.server.core.Persistent;
import ru.arlen.task.server.core.Trade;

/**
 * ServerClientHandler
 */
public class ServerClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Persistent store;

  public ServerClientHandler(Persistent store) {
    this.store = store;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    long millis = msg.readLong();
    short tickerLen = msg.readShort();
    String ticker = msg.readCharSequence(tickerLen, US_ASCII).toString();
    double price = msg.readDouble();
    int size = msg.readInt();

    ZonedDateTime utc = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    store.push(new Trade(millis, ticker, price, size));
    logger.debug("ticker: {}, timestamp: {}, price: {}, size: {}", ticker, formatter.format(utc), price, size);
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