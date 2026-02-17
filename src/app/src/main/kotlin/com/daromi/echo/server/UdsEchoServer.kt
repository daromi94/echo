package com.daromi.echo.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollIoHandler
import io.netty.channel.epoll.EpollServerDomainSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueIoHandler
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel
import io.netty.channel.unix.DomainSocketAddress
import io.netty.channel.unix.DomainSocketChannel
import io.netty.channel.uring.IoUring
import io.netty.channel.uring.IoUringIoHandler
import io.netty.channel.uring.IoUringServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.io.File

class UdsEchoServer private constructor(
    private val socketPath: String,
) {
    companion object {
        @JvmStatic
        fun create(socketPath: String): UdsEchoServer = UdsEchoServer(socketPath)
    }

    fun start() {
        File(socketPath).delete()

        val (handlerFactory, channelClass) =
            when {
                IoUring.isAvailable() -> {
                    IoUringIoHandler.newFactory() to IoUringServerSocketChannel::class.java
                }

                Epoll.isAvailable() -> {
                    EpollIoHandler.newFactory() to EpollServerDomainSocketChannel::class.java
                }

                KQueue.isAvailable() -> {
                    KQueueIoHandler.newFactory() to KQueueServerDomainSocketChannel::class.java
                }

                else -> {
                    throw IllegalStateException("Unsupported OS: UDS requires Epoll (Linux) or KQueue (macOS/BSD)")
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
                .childHandler(UdsEchoServerChannelInitializer(serverHandler))

            val future = boostrap.bind(DomainSocketAddress(socketPath)).sync()

            future.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully().sync()
            bossGroup.shutdownGracefully().sync()
        }
    }
}

private class UdsEchoServerChannelInitializer(
    val serverHandler: EchoServerHandler,
) : ChannelInitializer<DomainSocketChannel>() {
    override fun initChannel(ch: DomainSocketChannel) {
        val pipeline = ch.pipeline()

        pipeline.addLast(LoggingHandler(LogLevel.INFO))
        pipeline.addLast(serverHandler)
    }
}
