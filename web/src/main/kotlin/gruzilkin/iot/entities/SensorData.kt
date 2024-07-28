package gruzilkin.iot.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "sensor_data")
data class SensorData(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(name = "device_id")
    val deviceId: Int,

    @Column(name = "sensor_name")
    val sensorName: String,

    @Column(name = "sensor_value")
    val sensorValue: Double,

    @Column(name = "received_at")
    val receivedAt: LocalDateTime
)