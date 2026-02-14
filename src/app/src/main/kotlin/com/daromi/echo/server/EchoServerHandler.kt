package com.daromi.echo.server

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

@Sharable
class EchoServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(
        ctx: ChannelHandlerContext,
        msg: Any,
    ) {
        val input = msg as ByteBuf

        ctx.write(input)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        val future = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)

        future.addListener(ChannelFutureListener.CLOSE)
    }

    override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        cause.printStackTrace()

        ctx.close()
    }
}
