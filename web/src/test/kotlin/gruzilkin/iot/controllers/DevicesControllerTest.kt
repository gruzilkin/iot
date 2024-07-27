package gruzilkin.iot.controllers

import gruzilkin.iot.entities.Device
import gruzilkin.iot.repositories.DeviceRepository
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.assertTrue


@SpringBootTest
@AutoConfigureMockMvc
class DevicesControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var devicesRepository: DeviceRepository

    @Test
    @WithMockUser(username = "1")
    @DirtiesContext
    fun `save device`() {
        mockMvc.post("/devices") {
            with(csrf())
            param("name", "test")
        }.andExpect {
            status { isOk() }
            model {
                attributeExists("devices")
                attribute("devices", hasSize<List<Device>>(1))
            }
        }.andDo {
            print()
        }

        assertTrue { devicesRepository.findAll().any { it.name == "test" } }
    }

    @Test
    @WithMockUser(username = "1")
    fun `getDevice on non-existing device returns 404`() {
        mockMvc.get("/devices/1").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser(username = "1")
    @DirtiesContext
    fun `getDevice return device and tokens`() {
        mockMvc.post("/devices") {
            with(csrf())
            param("name", "test")
        }
        mockMvc.get("/devices/1").andExpect {
            status { isOk() }
            model {
                attributeExists("device")
            }
        }
    }
}