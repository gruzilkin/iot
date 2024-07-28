package gruzilkin.iot.ws

import gruzilkin.iot.entities.SensorData
import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.services.DeviceService
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TelemetryWebSockHandlerTest {
    @LocalServerPort
    var port: Int = 0

    @MockBean
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

        // mock sensorDataRepository.save and inject a latch to wait for the save operation
        val latch = CountDownLatch(1)
        var savedData: SensorData? = null
        whenever(sensorDataRepository.save(any<SensorData>())).thenAnswer { invocation ->
            savedData = invocation.getArgument(0)
            latch.countDown()
            savedData
        }

        // finally execute the call with everything ready
        StandardWebSocketClient().execute(handler, header, url)

        // websocket call is async so we wait for repository call
        latch.await(5, TimeUnit.SECONDS)

        // assert the saved data
        assertEquals("ppm", savedData!!.sensorName)
        assertTrue { Math.abs(savedData!!.sensorValue - 414.1) < 1e-6 }
        assertEquals(device.id, savedData!!.deviceId)
    }
}