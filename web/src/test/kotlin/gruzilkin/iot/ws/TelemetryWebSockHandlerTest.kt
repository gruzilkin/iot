package gruzilkin.iot.ws

import gruzilkin.iot.BaseTestClass
import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.services.DeviceService
import gruzilkin.iot.services.SensorDataService
import org.junit.jupiter.api.Disabled
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.math.BigDecimal
import java.net.URI
import java.security.Principal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/cleanup.sql")
class TelemetryWebSockHandlerTest : BaseTestClass() {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var sensorDataRepository: SensorDataRepository

    @SpyBean
    lateinit var sensorDataService: SensorDataService

    @Autowired
    lateinit var deviceService: DeviceService

    @Test
    fun testWebSocketInteraction() {
        // state preparation
        val user = Principal { "1" }
        val device = deviceService.save(user, "test")
        val token = deviceService.generateAndSaveToken(user, device.id!!)
        val message = """{"ppm": 414.1}"""

        val latch = CountDownLatch(1)

        // Set up spy to trigger when data is saved
        doAnswer { invocation ->
            invocation.callRealMethod()
            latch.countDown()
        }.whenever(sensorDataService).saveSensorData(any())

        // web socket client setup
        val handler = object : TextWebSocketHandler() {
            override fun afterConnectionEstablished(session: WebSocketSession) {
                session.sendMessage(TextMessage(message))
                session.close()
            }
        }
        val url = URI("ws://localhost:$port/ws/telemetry")
        val headers = WebSocketHttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }

        // execute the call
        StandardWebSocketClient().execute(handler, headers, url).get(5, TimeUnit.SECONDS)

        // wait for data to be saved (much shorter timeout)
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Data should be saved within 10 seconds")

        // verify the data
        val data = sensorDataRepository.findAll()
        assertEquals(1, data.size)
        assertEquals("ppm", data[0].sensorName)
        assertEquals(0, BigDecimal.valueOf(414.1).compareTo(data[0].sensorValue))
        assertEquals(device.id, data[0].deviceId)
    }

    @Test
    fun `websocket rejects connection with invalid token`() {
        val url = URI("ws://localhost:$port/ws/telemetry")
        val headers = WebSocketHttpHeaders().apply {
            add("Authorization", "Bearer invalid-token")
        }

        val client = StandardWebSocketClient()

        // WebSocket handshake failure should throw an exception immediately
        assertFailsWith<Exception> {
            client.execute(TextWebSocketHandler(), headers, url).get(5, TimeUnit.SECONDS)
        }
    }
}