package gruzilkin.iot.controllers

import gruzilkin.iot.repositories.DeviceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
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
    fun `save device`() {
        mockMvc.post("/devices") {
            with(csrf())
            param("name", "test")
        }.andExpect {
            status { isOk() }
        }.andDo {
            print()
        }

        assertTrue { devicesRepository.findAll().any { it.name == "test" } }
    }
}