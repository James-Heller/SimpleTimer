package space.jamestang.simpletimer.network

import java.nio.ByteBuffer

data class Message(
    val magic: Int = 0x7355608,
    val version: Int = 1,
    val type: Int,
    val topicLength: Int,
    val topic: String,
    val delay: Long,
    val payload: ByteArray
) {

    /**
     * Message types:
     * 1 - PING/PONG
     * 2 - Schedule task
     * 3 - Triggered
     */
    companion object {
        fun createPONG(topic: String?): Message {
            return Message(
                type = 1, // PONG type
                topicLength = topic?.length ?: 0,
                topic = topic?: "",
                delay = 0L, // No delay for PONG
                payload = "PONG".toByteArray() // No payload for PONG
            )
        }

        fun createScheduled(topic: String, taskId: Long): Message {
            return Message(
                type = 2, // Scheduled type
                topicLength = topic.length,
                topic = topic,
                delay = 0L, // No delay for scheduled messages
                payload = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(taskId).array()
            )
        }

        fun createTriggered(topic: String, payload: ByteArray): Message {
            return Message(
                type = 3, // Triggered type
                topicLength = topic.length,
                topic = topic,
                delay = 0L, // No delay for triggered messages
                payload = payload
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (magic != other.magic) return false
        if (version != other.version) return false
        if (type != other.type) return false
        if (topicLength != other.topicLength) return false
        if (topic != other.topic) return false
        if (delay != other.delay) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = magic
        result = 31 * result + version
        result = 31 * result + type
        result = 31 * result + topicLength
        result = 31 * result + topic.hashCode()
        result = 31 * result + delay.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Message(magic=$magic, version=$version, type=$type, topicLength=$topicLength, topic='$topic', delay=${delay}, payload=${payload.toString(
            Charsets.UTF_8)})"
    }
}