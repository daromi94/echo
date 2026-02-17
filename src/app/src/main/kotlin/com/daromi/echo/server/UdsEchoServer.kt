package com.daromi.echo.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.unix.DomainSocketAddress
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class UdsEchoServer private constructor(
    private val socketPath: String,
) {
    companion object {
        @JvmStatic
        fun create(socketPath: String): UdsEchoServer = UdsEchoServer(socketPath)
    }

    fun start() {
        val elg = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

        val handler = EchoServerHandler()

        try {
            val boostrap = ServerBootstrap()

            boostrap
                .group(elg)
                .channel(NioServerSocketChannel::class.java)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(UnixEchoServerChannelInitializer(handler))

            val future = boostrap.bind(DomainSocketAddress(socketPath)).sync()

            future.channel().closeFuture().sync()
        } finally {
            elg.shutdownGracefully().sync()
        }
    }
}

private class UnixEchoServerChannelInitializer(
    val handler: EchoServerHandler,
) : ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
        ch.pipeline().addLast(handler)
    }
}
