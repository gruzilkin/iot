package gruzilkin.iot.ws

import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.services.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URI
import java.security.Principal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TelemetryWebSockHandlerTest {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var sensorDataRepository: SensorDataRepository

    @Autowired
    lateinit var deviceService: DeviceService

    @Test
    @DirtiesContext
    fun testWebSocketInteraction() {
        // state preparation
        val user = Principal { "1" }
        val device = deviceService.save(user, "test")
        val token = deviceService.generateAndSaveToken(user, device.id!!)
        val message = """{"ppm": 414.1}"""

        val latch = CountDownLatch(1)

        // web socket client setup
        val handler = object : TextWebSocketHandler() {
            override fun afterConnectionEstablished(session: WebSocketSession) {
                session.sendMessage(TextMessage(message))
            }

            override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
                latch.countDown()
                session.close()
            }
        }
        val url = URI("ws://localhost:$port/ws/telemetry")
        val header = object : WebSocketHttpHeaders() {
            init {
                add("Authorization", "Bearer $token")
            }
        }

        // finally execute the call with everything ready
        StandardWebSocketClient().execute(handler, header, url)

        // websocket call is async so we wait for repository call
        latch.await(5, TimeUnit.SECONDS)

        val data = sensorDataRepository.findAll()

        // assert the saved data
        assertEquals(1, data.size)
        assertEquals("ppm", data[0].sensorName)
        assertTrue { Math.abs(data[0].sensorValue - 414.1) < 1e-6 }
        assertEquals(device.id, data[0].deviceId)
    }
}