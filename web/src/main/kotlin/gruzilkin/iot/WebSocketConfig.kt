package gruzilkin.iot

import gruzilkin.iot.ws.AuthenticationHandshakeHandler
import gruzilkin.iot.ws.RealtimeWebSockHandler
import gruzilkin.iot.ws.TelemetryWebSockHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig (
    val realtimeWebSockHandler: RealtimeWebSockHandler,
    val telemetryWebSockHandler: TelemetryWebSockHandler,
    val authenticationHandshakeHandler: AuthenticationHandshakeHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(telemetryWebSockHandler, "/ws/telemetry")
            .addInterceptors(authenticationHandshakeHandler)
        registry.addHandler(realtimeWebSockHandler, "/chart/{id}/realtime")
    }
}