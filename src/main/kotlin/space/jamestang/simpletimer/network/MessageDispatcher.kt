package space.jamestang.simpletimer.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class MessageDispatcher: ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

        if (msg !is Message){
            ctx.fireChannelRead(msg)
            return
        }

        when(msg.type){
            1 -> {

                val pong = Message.createPONG(null)
                ctx.writeAndFlush(pong)
            }


        }

        super.channelRead(ctx, msg)
    }
}