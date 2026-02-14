package com.daromi.echo.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class TcpEchoServer private constructor(
    private val port: Int,
) {
    companion object {
        @JvmStatic
        fun create(port: Int): TcpEchoServer = TcpEchoServer(port)
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
                .childHandler(TcpEchoServerChannelInitializer(handler))

            val future = boostrap.bind(port).sync()

            future.channel().closeFuture().sync()
        } finally {
            elg.shutdownGracefully().sync()
        }
    }
}

private class TcpEchoServerChannelInitializer(
    val handler: EchoServerHandler,
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(handler)
    }
}
