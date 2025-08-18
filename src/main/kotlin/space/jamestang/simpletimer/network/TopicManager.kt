package space.jamestang.simpletimer.network

import io.netty.channel.Channel

object TopicManager {
    private val topicChannels = mutableMapOf<String, MutableSet<Channel>>()

    fun subscribe(topic: String, channel: Channel) {
        topicChannels.computeIfAbsent(topic) { mutableSetOf() }.add(channel)
    }

    fun unsubscribe(topic: String, channel: Channel) {
        topicChannels[topic]?.remove(channel)
        if (topicChannels[topic].isNullOrEmpty()) {
            topicChannels.remove(topic)
        }
    }

    fun publish(topic: String, message: Message) {
        topicChannels[topic]?.forEach { channel ->
            if (channel.isActive) {
                channel.writeAndFlush(message)
            } else {
                unsubscribe(topic, channel)
            }
        }
    }
}