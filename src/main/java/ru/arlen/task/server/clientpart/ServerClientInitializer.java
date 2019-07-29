package ru.arlen.task.server.clientpart;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.arlen.task.server.core.InMemoryStore;

public class ServerClientInitializer extends ChannelInitializer<SocketChannel> {
    private final InMemoryStore store;
    private final EventExecutorGroup executers;

    public ServerClientInitializer(EventExecutorGroup executers, InMemoryStore store) {
        this.store = store;
        this.executers = executers;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new FrameDecoder()).addLast(executers, new ServerClientHandler(store));
    }
}
