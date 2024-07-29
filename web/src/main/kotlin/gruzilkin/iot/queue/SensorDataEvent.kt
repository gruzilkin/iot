package gruzilkin.iot.queue

data class SensorDataEvent(
    val deviceId: Long,
    val sensorName: String,
    val sensorValue: Double,
    val receivedAt: Long
)