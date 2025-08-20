package space.jamestang.simpletimer.network

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class SimpleTimerChannelInitializer: ChannelInitializer<SocketChannel>() {

    private val MAX_FRAME_LENGTH = 1024 * 1024 * 10 // 10 MB

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().apply {
            addLast(LoggingHandler(LogLevel.TRACE))
            addLast(LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, 4, 0, 4))
            addLast(MessageDecoder())
            addLast(LengthFieldPrepender(4))
            addLast(MessageEncoder())
            addLast(MessageDispatcher())


        }
    }
}