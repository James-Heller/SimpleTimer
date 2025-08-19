package space.jamestang.simpletimer.network

import io.netty.channel.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object TopicManager {

    private val topicChannels = mutableMapOf<String, MutableSet<ChannelInfo>>()
    private const val HEARTBEAT_TIMEOUT = 30000 // 30 seconds
    private val logger: Logger = LoggerFactory.getLogger(TopicManager::class.java)

    init {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(::removeTimeoutChannels,
            HEARTBEAT_TIMEOUT.toLong(), HEARTBEAT_TIMEOUT.toLong(),
            TimeUnit.MILLISECONDS)
    }

    fun subscribe(topic: String, channel: Channel) {
        val info = ChannelInfo(channel, System.currentTimeMillis())
        topicChannels.computeIfAbsent(topic) { mutableSetOf() }.add(info)
    }

    fun unsubscribe(topic: String, channel: Channel) {
        topicChannels[topic]?.removeIf { it.channel == channel }
        if (topicChannels[topic].isNullOrEmpty()) {
            topicChannels.remove(topic)
        }
    }

    fun publish(topic: String, message: Message) {
        topicChannels[topic]?.forEach { info ->
            if (info.channel.isActive) {
                info.channel.writeAndFlush(message)
            } else {
                unsubscribe(topic, info.channel)
                logger.warn("Channel ${info.channel.id()} is inactive, removing from topic $topic.")
            }
        }
    }

    fun heartbeat(topic: String, channel: Channel) = topicChannels[topic]?.find { it.channel == channel }?.lastHeartbeat = System.currentTimeMillis()

    fun removeTimeoutChannels() {
        val now = System.currentTimeMillis()
        logger.debug("Removing channels that have not sent heartbeat in the last $HEARTBEAT_TIMEOUT milliseconds.")
        topicChannels.forEach { (_, set) ->
            set.removeIf { now - it.lastHeartbeat > HEARTBEAT_TIMEOUT }
        }
    }
}