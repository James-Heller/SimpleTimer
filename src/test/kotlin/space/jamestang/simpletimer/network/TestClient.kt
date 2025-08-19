package space.jamestang.simpletimer.network

import org.junit.jupiter.api.BeforeEach
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.test.Test

class TestClient {

    private val HOST = "localhost"
    private val PORT = 8080
    private lateinit var socket: Socket
    private lateinit var dis: DataInputStream
    private lateinit var dos: DataOutputStream

    private fun prepareMessage(message: Message): ByteArray {
        val topicBytes = message.topic.toByteArray(Charsets.UTF_8)
        val payloadBytes = message.payload
        val bodyLength = 4 + 4 + 4 + 4 + topicBytes.size + 8 + payloadBytes.size // magic, version, type, topicLength, topic, delay, payload

        val buffer = ByteBuffer.allocate(4 + bodyLength) // 4字节长度 + body
        buffer.putInt(bodyLength) // 消息长度（不含自身）
        buffer.putInt(message.magic)
        buffer.putInt(message.version)
        buffer.putInt(message.type)
        buffer.putInt(topicBytes.size)
        buffer.put(topicBytes)
        buffer.putLong(message.delay)
        buffer.put(payloadBytes)
        return buffer.array()
    }

    private fun readMessage(input: DataInputStream): Message {
        val length = input.readInt() // 读取消息长度
        val magic = input.readInt()
        val version = input.readInt()
        val type = input.readInt()
        val topicLength = input.readInt()
        val topicBytes = ByteArray(topicLength)
        input.readFully(topicBytes)
        val topic = String(topicBytes, Charsets.UTF_8)
        val delay = input.readLong()
        val payloadLength = length - (4 + 4 + 4 + 4 + topicLength + 8) // 减去已读取的部分长度
        val payload = ByteArray(payloadLength)
        input.readFully(payload)

        return Message(magic, version, type, topicLength, topic, delay, payload)
    }

    @BeforeEach
    fun setup() {
        socket = Socket(HOST, PORT)
        dis = DataInputStream(socket.getInputStream())
        dos = DataOutputStream(socket.getOutputStream())
    }

    @Test
    fun testScheduleTask() {

        val ping = Message.createPONG("test-topic")
        val pingBytes = prepareMessage(ping)
        dos.write(pingBytes)
        dos.flush()
        val r = readMessage(dis)
        println(r)

        val task = Message(
            magic = 0x7355608,
            version = 1,
            type = 2,
            topicLength = "test-topic".length,
            topic = "test-topic",
            delay = 5000L, // 5 seconds delay
            payload = "This is a scheduled task".toByteArray(Charsets.UTF_8)
        )
        val taskBytes = prepareMessage(task)
        dos.write(taskBytes)
        dos.flush()
        val response = readMessage(dis)
        println(response)
        val callback = readMessage(dis)
        println(callback)

        dis.close()
        dos.close()
        socket.close()
    }

}