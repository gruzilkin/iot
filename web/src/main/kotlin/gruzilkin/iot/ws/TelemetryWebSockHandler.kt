package gruzilkin.iot.ws

import gruzilkin.iot.entities.SensorData
import gruzilkin.iot.repositories.SensorDataRepository
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class TelemetryWebSockHandler(val sensorDataRepository: SensorDataRepository) : TextWebSocketHandler() {
    override fun handleTextMessage(webSocketSession: WebSocketSession, message: TextMessage) {
        val deviceId = webSocketSession.attributes["deviceId"] as Long
        val payload: Map<String, String> = Json.decodeFromString(message.payload)

        for ((key, value) in payload) {
            sensorDataRepository.save(
                SensorData(
                    deviceId = deviceId.toInt(),
                    sensorName = key,
                    sensorValue = value.toDouble(),
                    receivedAt = java.time.LocalDateTime.now()
                )
            )
        }
    }
}