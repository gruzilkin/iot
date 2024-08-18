package gruzilkin.iot.ws

import com.google.gson.Gson
import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.services.DeviceService
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.math.BigDecimal
import java.math.RoundingMode


@Component
class RealtimeWebSockHandler(
    val rabbitAdmin: RabbitAdmin,
    val rabbitTemplate: RabbitTemplate,
    val deviceService: DeviceService
) : TextWebSocketHandler() {
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val deviceId = session.uri?.path?.split("/")?.takeLast(2)?.firstOrNull()?.toLongOrNull()
        if (deviceId == null) {
            session.close()
            return
        }

        if (session.principal == null) {
            session.close()
            return
        }

        if (!deviceService.canAccess(session.principal!!, deviceId)) {
            session.close()
            return
        }

        val queue = Queue("", false, true, true);
        val queueName = rabbitAdmin.declareQueue(queue)

        val exchange = TopicExchange("amq.topic")
        val routingKey = "sensor.data.$deviceId.*"

        val binding = BindingBuilder.bind(queue)
            .to(exchange)
            .with(routingKey)
        rabbitAdmin.declareBinding(binding)

        val container = SimpleMessageListenerContainer()
        container.connectionFactory = rabbitTemplate.connectionFactory
        container.setQueueNames(queueName)
        container.setMessageListener { message ->
            if (!session.isOpen) {
                container.stop()
            }
            else {
                val messageBody = String(message.body)
                val event = Gson().fromJson(messageBody, SensorDataEvent::class.java)
                val wsMessage = mapOf(
                    event.sensorName to event.sensorValue.setScale(3, RoundingMode.FLOOR).toDouble(),
                    "receivedAt" to event.receivedAt)
                session.sendMessage(TextMessage(Gson().toJson(wsMessage)))
            }
        }
        container.start()
    }
}