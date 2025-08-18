package space.jamestang.simpletimer.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageDecoder: ByteToMessageDecoder() {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        logger.debug("Received data of {} bytes.", `in`.readableBytes())

        if (`in`.readableBytes() < 4) {
            logger.warn("Not enough data to read the length of the message.")
            return
        }

        val magicNumber = `in`.readInt()
        if (magicNumber != 0x7355608) {
            logger.error("Invalid magic number: $magicNumber")
            ctx.close()
            return
        }
        val version = `in`.readInt()
        val type = `in`.readInt()
        val topicLength = `in`.readInt()
        val topicBytes = ByteArray(topicLength)
        `in`.readBytes(topicBytes)
        val topic = String(topicBytes, Charsets.UTF_8)
        val payloadLength = `in`.readableBytes()
        if (payloadLength < 0) {
            logger.error("Payload length is negative: $payloadLength")
            ctx.close()
            return
        }

        TopicManager.subscribe(topic, ctx.channel())

        val payload = ByteArray(payloadLength)
        `in`.readBytes(payload)
        val data = Message(
            magic = magicNumber,
            version = version,
            type = type,
            topicLength = topicLength,
            topic = topic,
            payload = payload
        )
        out.add(data)
        logger.debug("Decoded message: $data")
    }
}