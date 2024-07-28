package gruzilkin.iot.ws

import gruzilkin.iot.repositories.TokenRepository
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor


@Component
class AuthenticationHandshakeHandler(val tokenRepository: TokenRepository) : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        try {
            val authorization = request.headers["Authorization"] ?: throw IllegalAccessException("No authorization header")
            val token = authorization[0].split(" ")[1]
            val tokenEntity = tokenRepository.findById(token).orElseThrow { IllegalAccessException("Token not found") }
            attributes["deviceId"] = tokenEntity.deviceId
            return true
        }
        catch (e: Exception) {
            println(e)
            response.setStatusCode(HttpStatus.FORBIDDEN)
            return false
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) { }

}