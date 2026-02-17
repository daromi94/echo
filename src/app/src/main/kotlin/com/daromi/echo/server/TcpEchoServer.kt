package com.daromi.echo.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollIoHandler
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueIoHandler
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.uring.IoUring
import io.netty.channel.uring.IoUringIoHandler
import io.netty.channel.uring.IoUringServerSocketChannel
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
        val (handlerFactory, channelClass) =
            when {
                IoUring.isAvailable() -> {
                    IoUringIoHandler.newFactory() to IoUringServerSocketChannel::class.java
                }

                Epoll.isAvailable() -> {
                    EpollIoHandler.newFactory() to EpollServerSocketChannel::class.java
                }

                KQueue.isAvailable() -> {
                    KQueueIoHandler.newFactory() to KQueueServerSocketChannel::class.java
                }

                else -> {
                    NioIoHandler.newFactory() to NioServerSocketChannel::class.java
                }
            }

        val bossGroup = MultiThreadIoEventLoopGroup(handlerFactory)
        val workerGroup = MultiThreadIoEventLoopGroup(handlerFactory)

        val serverHandler = EchoServerHandler()

        try {
            val boostrap = ServerBootstrap()

            boostrap
                .group(bossGroup, workerGroup)
                .channel(channelClass)
                .childHandler(TcpEchoServerChannelInitializer(serverHandler))

            val future = boostrap.bind(port).sync()

            future.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully().sync()
            bossGroup.shutdownGracefully().sync()
        }
    }
}

private class TcpEchoServerChannelInitializer(
    val serverHandler: EchoServerHandler,
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()

        pipeline.addLast(LoggingHandler(LogLevel.INFO))
        pipeline.addLast(serverHandler)
    }
}
