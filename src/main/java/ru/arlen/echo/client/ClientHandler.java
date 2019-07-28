// package nett.client;

// import java.util.List;

// import io.netty.buffer.ByteBuf;
// import io.netty.channel.ChannelHandlerContext;
// import io.netty.handler.codec.ByteToMessageDecoder;

// /**
//  * ClientHandler
//  */
// public class ClientHandler extends ByteToMessageDecoder { // (1)
//     @Override
//     protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)
//         if (in.readableBytes() < 26) {
//             return; // (3)
//         }
        
//         System.out.println(in.readBytes(26));
//         // out.add(in.readBytes(26)); // (4)
//     }
// }