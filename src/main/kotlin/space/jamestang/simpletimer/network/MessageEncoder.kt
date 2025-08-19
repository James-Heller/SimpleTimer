package space.jamestang.simpletimer.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class MessageEncoder: MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {

        out.writeInt(msg.magic)
        out.writeInt(msg.version)
        out.writeInt(msg.type)
        out.writeInt(msg.topicLength)
        out.writeBytes(msg.topic.toByteArray(Charsets.UTF_8))
        out.writeLong(msg.delay)
        out.writeBytes(msg.payload)
    }
}