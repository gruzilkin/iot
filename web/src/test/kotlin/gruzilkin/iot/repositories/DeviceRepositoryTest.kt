package gruzilkin.iot.repositories

import gruzilkin.iot.entities.Device
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertNotNull

@DataJpaTest
class DeviceRepositoryTest {

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    @Test
    @DirtiesContext
    fun `should save device`() {
        val device = Device(userId = 1, name = "test")
        val savedDevice = deviceRepository.save(device)
        assertEquals(savedDevice.userId, device.userId)
        assertEquals(savedDevice.name, device.name)
        assertNotNull(savedDevice.id)
    }

    @Test
    @DirtiesContext
    fun `findAllByUserIdOrderById should work`() {
        deviceRepository.save(Device(userId = 1, name = "test"))
        deviceRepository.save(Device(userId = 1, name = "test2"))
        val devices = deviceRepository.findAllByUserIdOrderById(1)
        assertEquals(2, devices.size)
    }
}
