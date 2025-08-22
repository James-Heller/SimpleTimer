package space.jamestang.simpletimer

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.jamestang.simpletimer.network.SimpleTimerChannelInitializer

class SimplerTimer {
    private val bossEventLoopGroup: EventLoopGroup
    private val workerEventLoopGroup: EventLoopGroup
    private val server: ServerBootstrap
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        val ioThreadFactory = NioIoHandler.newFactory()
        bossEventLoopGroup = MultiThreadIoEventLoopGroup(ioThreadFactory)
        workerEventLoopGroup = MultiThreadIoEventLoopGroup(ioThreadFactory)

        server = ServerBootstrap()
            .group(bossEventLoopGroup, workerEventLoopGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(SimpleTimerChannelInitializer())
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
    }

    fun run(port: Int = 8080){
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Shutdown signal received, stopping server...")
            workerEventLoopGroup.shutdownGracefully()
            bossEventLoopGroup.shutdownGracefully()
            logger.info("Netty event loop groups shut down.")
        })

        val future = server.bind("0.0.0.0",port).sync()

        if (future.isSuccess) {
            logger.info("Server started on port $port")
        } else {
            logger.error("Failed to start server on port $port")
            return
        }

        future.channel().closeFuture().sync()
        logger.info("Server stopped.")
        workerEventLoopGroup.shutdownGracefully()
        bossEventLoopGroup.shutdownGracefully()
    }
}