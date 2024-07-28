package gruzilkin.iot.ws

import gruzilkin.iot.repositories.TokenRepository
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class TelemetryWebSockHandler(val tokenRepository: TokenRepository) : TextWebSocketHandler() {
    override fun handleTextMessage(webSocketSession: WebSocketSession, message: TextMessage) {
        val deviceId = webSocketSession.attributes["deviceId"] as Long
        println("[$deviceId]Received message: $message")
    }
}