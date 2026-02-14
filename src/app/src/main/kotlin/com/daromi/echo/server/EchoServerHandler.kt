package com.daromi.echo.server

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

@Sharable
class EchoServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(
        ctx: ChannelHandlerContext,
        msg: Any,
    ) {
        ctx.write(msg)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        cause.printStackTrace()

        ctx.close()
    }
}
