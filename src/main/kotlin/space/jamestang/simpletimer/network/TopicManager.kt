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
            logger.debug("Trying to publish message to topic {} on channel {}.", topic, info.channel?.id())
            if (info.channel?.isActive == true) {
                info.channel.writeAndFlush(message)
            }
        }
    }

    fun heartbeat(channel: Channel) {
        val now = System.currentTimeMillis()
        topicChannels.values.forEach { set ->
            set.find { it.channel == channel }?.lastHeartbeat = now
        }
    }

    fun removeTimeoutChannels() {
        val now = System.currentTimeMillis()
        var count = 0
        topicChannels.forEach { (_, set) ->
            set.forEach {
                if (now - it.lastHeartbeat > HEARTBEAT_TIMEOUT) {
                    it.channel?.close()
                    set.remove(it)
                    count++
                }
            }
        }

        logger.debug("Removed {} channels due to heartbeat timeout.", count)
    }
}