package space.jamestang.simpletimer.network

import io.netty.channel.Channel

data class ChannelInfo(val channel: Channel?, var lastHeartbeat: Long){

    override fun equals(other: Any?): Boolean {
        return this === other || (other is ChannelInfo && this.channel == other.channel)
    }

    override fun hashCode(): Int {
        return this.channel?.hashCode() ?: 0
    }
}
