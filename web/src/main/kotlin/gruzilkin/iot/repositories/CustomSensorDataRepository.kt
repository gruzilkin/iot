package gruzilkin.iot.repositories

import java.math.BigDecimal
import java.time.Instant

interface CustomSensorDataRepository {
    fun smartFindByDeviceIdAndSensorName(deviceId: Long, sensorName: String, start: Instant, end: Instant, limit: Int): List<Pair<Instant, BigDecimal>>
}