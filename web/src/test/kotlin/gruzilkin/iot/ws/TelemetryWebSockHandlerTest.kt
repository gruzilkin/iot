package gruzilkin.iot.ws

import gruzilkin.iot.BaseTestClass
import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.services.DeviceService
import gruzilkin.iot.services.SensorDataService
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URI
import java.security.Principal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Ignore
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
                session.close()
            }
        }
        val url = URI("ws://localhost:$port/ws/telemetry")
        val header = object : WebSocketHttpHeaders() {
            init {
                add("Authorization", "Bearer $token")
            }
        }

        doAnswer { invocation ->
            invocation.callRealMethod()
            latch.countDown()
        }.whenever(sensorDataService).saveSensorData(any())

        // finally execute the call with everything ready
        StandardWebSocketClient().execute(handler, header, url)

        // websocket call is async so we wait for repository call
        latch.await(60, TimeUnit.SECONDS)

        val data = sensorDataRepository.findAll()

        // assert the saved data
        assertEquals(1, data.size)
        assertEquals("ppm", data[0].sensorName)
        assertTrue { Math.abs(data[0].sensorValue - 414.1) < 1e-6 }
        assertEquals(device.id, data[0].deviceId)
    }

    @Test
    @DirtiesContext
    fun `websocket rejects connection without authentication`() {
        val latch = CountDownLatch(1)

        val url = URI("ws://localhost:$port/ws/telemetry")
        val header = object : WebSocketHttpHeaders() {
            init {
                add("Authorization", "Bearer non-existing-token")
            }
        }

        var errorMessage: String? = null
        StandardWebSocketClient().execute(TextWebSocketHandler(), header, url).exceptionally { e ->
            errorMessage = e.message
            latch.countDown()
            null
        }.get()

        latch.await(5, TimeUnit.SECONDS)

        assertTrue {
            errorMessage?.contains("403") ?: false
        }
    }
}