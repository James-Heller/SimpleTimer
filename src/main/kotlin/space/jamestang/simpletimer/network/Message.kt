package space.jamestang.simpletimer.network

data class Message(
    val magic: Int = 0x7355608,
    val version: Int = 1,
    val type: Int,
    val topicLength: Int,
    val topic: String,
    val payload: ByteArray
) {

    companion object {
        fun createPONG(topic: String?): Message {
            return Message(
                type = 1, // PONG type
                topicLength = topic?.length ?: 0,
                topic = topic?: "",
                payload = "PONG".toByteArray() // No payload for PONG
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
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = magic
        result = 31 * result + version
        result = 31 * result + type
        result = 31 * result + topicLength
        result = 31 * result + topic.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Message(magic=$magic, version=$version, type=$type, topicLength=$topicLength, topic='$topic', payload=${payload.toString(
            Charsets.UTF_8)})"
    }
}