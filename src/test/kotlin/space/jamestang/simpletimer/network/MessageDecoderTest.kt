package space.jamestang.simpletimer.network

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Test
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class MessageDecoderTest {
    @Test
    fun `test decode valid message`() {
        val magicNumber = 0x7355608
        val version = 1
        val type = 1
        val topic = ""
        val topicBytes = topic.toByteArray(Charsets.UTF_8)
        val topicLength = topicBytes.size
        val payload = "PING".toByteArray(Charsets.UTF_8)

        // 构造消息体
        val bodyBuf = Unpooled.buffer()
        bodyBuf.writeInt(magicNumber)
        bodyBuf.writeInt(version)
        bodyBuf.writeInt(type)
        bodyBuf.writeInt(topicLength)
        bodyBuf.writeBytes(topicBytes)
        bodyBuf.writeBytes(payload)

        // 构造总帧（LengthFieldBasedFrameDecoder 需要长度字段）
        val frameLength = bodyBuf.readableBytes()
        val buf = Unpooled.buffer()
        buf.writeInt(frameLength) // 长度字段
        buf.writeBytes(bodyBuf)

        val socket = Socket("localhost", 8080) // 模拟一个 Socket 连接
        val dos = DataOutputStream(socket.outputStream)
        dos.write(buf.array()) // 发送数据到服务器
        dos.flush()

        val dis = DataInputStream(socket.inputStream)
        val receivedLength = dis.readInt() // 读取长度字段
        val receivedMagic = dis.readInt()
        val receivedVersion = dis.readInt()
        val receivedType = dis.readInt()
        val receivedTopicLength = dis.readInt()
        val receivedTopicBytes = ByteArray(receivedTopicLength)
        dis.readFully(receivedTopicBytes)
        val receivedTopic = String(receivedTopicBytes, Charsets.UTF_8)
        val receivedPayload = ByteArray(receivedLength - 16 - receivedTopicLength) // 减去魔数、版本、类型、主题长度的字节数
        dis.readFully(receivedPayload)
        val receivedMessage = Message(
            magic = receivedMagic,
            version = receivedVersion,
            type = receivedType,
            topicLength = receivedTopicLength,
            topic = receivedTopic,
            payload = receivedPayload
        )
        println(receivedLength)
        println(receivedMessage)
        println(receivedMessage.payload.toString(Charsets.UTF_8))


    }
}
// ...可补充异常情况测试...
