package space.jamestang.simpletimer.network

import io.netty.channel.Channel

data class ChannelInfo(val channel: Channel, var lastHeartbeat: Long)
