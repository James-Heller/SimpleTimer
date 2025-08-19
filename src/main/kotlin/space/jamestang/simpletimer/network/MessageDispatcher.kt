package space.jamestang.simpletimer.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import space.jamestang.simpletimer.processor.PINGPONGProcessor
import space.jamestang.simpletimer.processor.ScheduleTaskProcessor

class MessageDispatcher: ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

        if (msg !is Message){
            ctx.fireChannelRead(msg)
            return
        }

        /**
         * Dispatch message based on its type.
         * 1. PING - reply with PONG
         * 2. Schedule a task
         */
        when(msg.type){
            1 -> {
                val pong = PINGPONGProcessor.replyPONG(msg, ctx.channel())
                ctx.writeAndFlush(pong)
                return
            }

            2 -> {
                // Schedule a task
                val taskId = ScheduleTaskProcessor.scheduleTask(msg)
                val scheduledMsg = Message.createScheduled(msg.topic, taskId)
                ctx.writeAndFlush(scheduledMsg)
                return
            }

        }

        super.channelRead(ctx, msg)
    }
}