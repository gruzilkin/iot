package gruzilkin.iot.queue

import java.math.BigDecimal

data class SensorDataEvent(
    val deviceId: Long,
    val sensorName: String,
    val sensorValue: BigDecimal,
    val receivedAt: Long
)