package gruzilkin.iot.repositories

import gruzilkin.iot.entities.Device
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull

@DataJpaTest
@Sql("/cleanup.sql")
class DeviceRepositoryTest {

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    @Test
    fun `should save device`() {
        val device = Device(userId = 1, name = "test")
        val savedDevice = deviceRepository.save(device)
        assertEquals(savedDevice.userId, device.userId)
        assertEquals(savedDevice.name, device.name)
        assertNotNull(savedDevice.id)
    }

    @Test
    fun `findAllByUserIdOrderById should work`() {
        deviceRepository.save(Device(userId = 1, name = "test"))
        deviceRepository.save(Device(userId = 1, name = "test2"))
        val devices = deviceRepository.findAllByUserIdOrderById(1)
        assertEquals(2, devices.size)
    }
}
