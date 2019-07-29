package ru.arlen.task.server.clientpart;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import ru.arlen.task.server.clientpart.FrameDecoder.DecoderState;
import static ru.arlen.task.server.clientpart.FrameDecoder.DecoderState.READ_CONTENT;
import static ru.arlen.task.server.clientpart.FrameDecoder.DecoderState.READ_LENGTH;

/**
 * FrameHandler
 */
public class FrameDecoder extends ReplayingDecoder<DecoderState> {
    public enum DecoderState {
        READ_LENGTH, READ_CONTENT;
    }

    private int length;

    public FrameDecoder() {
        super(READ_LENGTH);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
        case READ_LENGTH:
            length = in.readShort();
            checkpoint(READ_CONTENT);
            break;
        case READ_CONTENT:
            out.add(in.readBytes(length));
            checkpoint(READ_LENGTH);
            break;
        default:
            throw new Error("Shouldn't reach here.");
        }
    }
}