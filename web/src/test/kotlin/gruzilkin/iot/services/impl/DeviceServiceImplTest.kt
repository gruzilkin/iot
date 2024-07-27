package gruzilkin.iot.services.impl

import gruzilkin.iot.services.DeviceService
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.security.Principal
import kotlin.test.*

@SpringBootTest
class DeviceServiceImplTest {
    @Autowired
    lateinit var deviceService: DeviceService

    @Test
    fun `find by id should not see other user devices`() {
        val user1 = Principal { "1" }
        val user2 = Principal { "2" }
        var device = deviceService.save(user1, "device_1")
        var readDevice = deviceService.findById(user2, device.id!!)
        assertNull(readDevice)
    }

    @Test
    fun `find all should not see other user devices`() {
        val user1 = Principal { "1" }
        val user2 = Principal { "2" }
        var device = deviceService.save(user1, "device_1")
        var readDevices = deviceService.findAll(user2)
        assertTrue { readDevices.isEmpty() }
    }
}