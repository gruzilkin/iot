package gruzilkin.iot.controllers

import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.repositories.DeviceRepository
import gruzilkin.iot.services.DeviceService
import gruzilkin.iot.services.SensorDataService
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.security.Principal

//@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@Sql("/cleanup.sql")
class ChartsControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var deviceService: DeviceService

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    @Autowired
    lateinit var sensorDataService: SensorDataService

    @Test
    @WithMockUser(username = "1")
    fun index() {
        val device = deviceService.save(Principal { "1" }, "device_1")
        sensorDataService.saveSensorData(SensorDataEvent(device.id!!, "temperature", 1.0, 1))
        sensorDataService.saveSensorData(SensorDataEvent(device.id!!, "temperature", 2.0, 2))

        mockMvc.get("/charts/${device.id}") {
            with(csrf())
        }.andExpect {
            status { isOk() }
            model {
                attributeExists("temperature")
                attribute("temperature", hasSize<List<*>>(2))
            }
        }.andDo {
            print()
        }
    }
}