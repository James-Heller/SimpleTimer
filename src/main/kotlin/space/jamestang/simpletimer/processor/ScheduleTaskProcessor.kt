package space.jamestang.simpletimer.processor

import io.netty.util.HashedWheelTimer
import io.netty.util.Timeout
import space.jamestang.simpletimer.network.Message
import space.jamestang.simpletimer.network.TopicManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
object ScheduleTaskProcessor {

    private val timer = HashedWheelTimer()
    private val taskIdGenerator = AtomicLong(0L)
    private val tasks = ConcurrentHashMap<Long, Timeout>()


    fun scheduleTask(msg: Message): Long {
        val taskId = taskIdGenerator.incrementAndFetch()
        val timeout = timer.newTimeout({
            val response = Message.createTriggered(msg.topic, msg.payload)
            TopicManager.publish(msg.topic, response)
            tasks.remove(taskId)
        }, msg.delay, TimeUnit.MILLISECONDS)
        tasks[taskId] = timeout
        return taskId
    }

    fun cancelTask(taskId: Long): Boolean {
        val timeout = tasks.remove(taskId)
        return timeout?.cancel() ?: false
    }
}