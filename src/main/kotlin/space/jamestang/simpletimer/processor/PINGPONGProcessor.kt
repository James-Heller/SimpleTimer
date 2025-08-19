package space.jamestang.simpletimer.processor

import io.netty.channel.Channel
import space.jamestang.simpletimer.network.Message
import space.jamestang.simpletimer.network.TopicManager

object PINGPONGProcessor {

    fun replyPONG(msg: Message, channel: Channel): Message{
        TopicManager.heartbeat(msg.topic, channel)
        return Message.createPONG(msg.topic)
    }
}