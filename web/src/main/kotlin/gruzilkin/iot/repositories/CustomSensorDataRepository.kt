package gruzilkin.iot.repositories

import java.math.BigDecimal
import java.time.Instant

interface CustomSensorDataRepository {
    data class Point(val receivedAt: Instant, val value: BigDecimal)
    fun smartFindByDeviceIdAndSensorName(deviceId: Long, sensorName: String, start: Instant, end: Instant, limit: Int): List<Point>
}