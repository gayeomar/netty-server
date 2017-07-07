package com.fdc.ucom.ref.netty.server;


import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.nio.charset.Charset;

/**
 * Handles a server-side channel.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("Got a message! " + buf.toString(Charset.defaultCharset()));
            buf.retain();
            Thread.sleep(10 * 1000);
            ctx.writeAndFlush(msg, new DefaultChannelPromise(ctx.channel()));
            System.out.println("Echoed message back to caller...");

        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}