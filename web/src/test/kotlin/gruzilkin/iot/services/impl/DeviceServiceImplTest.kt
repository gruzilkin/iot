package gruzilkin.iot.services.impl

import gruzilkin.iot.services.DeviceService
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.security.Principal
import kotlin.test.*

@SpringBootTest
class DeviceServiceImplTest {
    @Autowired
    lateinit var deviceService: DeviceService

    @Test
    @DirtiesContext
    fun `find by id should not see other user devices`() {
        val user1 = Principal { "1" }
        val user2 = Principal { "2" }
        var device = deviceService.save(user1, "device_1")
        var readDevice = deviceService.findById(user2, device.id!!)
        assertNull(readDevice)
    }

    @Test
    @DirtiesContext
    fun `delete by id should not see other user devices`() {
        val user1 = Principal { "1" }
        val user2 = Principal { "2" }
        val device = deviceService.save(user1, "device_1")
        assertFailsWith<IllegalArgumentException> {
            deviceService.deleteById(user2, device.id!!)
        }
    }

    @Test
    @DirtiesContext
    fun `delete by id green path`() {
        val user1 = Principal { "1" }
        val device = deviceService.save(user1, "device_1")
        deviceService.deleteById(user1, device.id!!)
        val readDevice = deviceService.findById(user1, device.id!!)
        assertNull(readDevice)
    }

    @Test
    @DirtiesContext
    fun `delete token green path`() {
        val user1 = Principal { "1" }
        var device = deviceService.save(user1, "device_1")
        val id = device.id!!

        val token = deviceService.generateAndSaveToken(user1, id)
        deviceService.deleteToken(user1, id, token)

        val readDevice = deviceService.findById(user1, id)!!
        assertTrue { readDevice.tokens.isEmpty() }
    }

    @Test
    fun canAccess() {
        val user1 = Principal { "1" }
        val user2 = Principal { "2" }
        val device = deviceService.save(user1, "device_1")
        assertTrue { deviceService.canAccess(user1, device.id!!) }
        assertFalse { deviceService.canAccess(user2, device.id!!) }
    }
}