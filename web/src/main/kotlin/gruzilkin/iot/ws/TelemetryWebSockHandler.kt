package gruzilkin.iot.ws

import com.google.gson.Gson
import gruzilkin.iot.queue.SensorDataEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.Instant

@Component
class TelemetryWebSockHandler(
    val rabbitTemplate: RabbitTemplate
) : TextWebSocketHandler() {
    override fun handleTextMessage(webSocketSession: WebSocketSession, message: TextMessage) {
        try {
            val deviceId = webSocketSession.attributes["deviceId"] as Long
            val payload = Gson().fromJson(message.payload, Map::class.java) as Map<String, Double>

            for ((key, value) in payload) {
                val routingKey = "sensor.data.$deviceId.$key"
                val event = SensorDataEvent(
                    deviceId = deviceId,
                    sensorName = key,
                    sensorValue = value,
                    receivedAt = Instant.now().toEpochMilli()
                )
                rabbitTemplate.send("amq.topic", routingKey, Message(Gson().toJson(event).toByteArray()))
            }
        }
        catch (e: Exception) {
            println(e)
            webSocketSession.close(CloseStatus.BAD_DATA)
        }
    }
}