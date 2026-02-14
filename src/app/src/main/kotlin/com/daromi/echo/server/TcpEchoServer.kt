package com.daromi.echo.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

class EchoServer private constructor(
    private val port: Int,
) {
    companion object {
        @JvmStatic
        fun create(port: Int): EchoServer = EchoServer(port)
    }

    fun start() {
        val elg = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

        val handler = EchoServerHandler()

        try {
            val boostrap = ServerBootstrap()

            boostrap
                .group(elg)
                .channel(NioServerSocketChannel::class.java)
                .localAddress(InetSocketAddress(port))
                .childHandler(EchoServerChannelInitializer(handler))

            val future = boostrap.bind().sync()

            future.channel().closeFuture().sync()
        } finally {
            elg.shutdownGracefully().sync()
        }
    }
}

private class EchoServerChannelInitializer(
    val handler: EchoServerHandler,
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(handler)
    }
}
