package gruzilkin.iot.services.impl

import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.services.SensorDataService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class SensorDataServiceImplTest {
    @Autowired
    lateinit var sensorDataService: SensorDataService

    @Test
    fun getDistinctDeviceSensorPairs() {
        sensorDataService.saveSensorData(
            SensorDataEvent(
                deviceId = 1,
                sensorName = "sensor_1",
                sensorValue = BigDecimal.valueOf(1),
                receivedAt = 1
            )
        )
        sensorDataService.saveSensorData(
            SensorDataEvent(
                deviceId = 1,
                sensorName = "sensor_1",
                sensorValue = BigDecimal.valueOf(2),
                receivedAt = 2
            )
        )
        sensorDataService.saveSensorData(
            SensorDataEvent(
                deviceId = 2,
                sensorName = "sensor_1",
                sensorValue = BigDecimal.valueOf(3),
                receivedAt = 3
            )
        )
        sensorDataService.saveSensorData(
            SensorDataEvent(
                deviceId = 2,
                sensorName = "sensor_2",
                sensorValue = BigDecimal.valueOf(4),
                receivedAt = 4
            )
        )

        val pairs = sensorDataService.getDistinctDeviceSensorPairs()
        kotlin.test.assertEquals(3, pairs.size)
    }
}