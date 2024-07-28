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
    val deviceId: Long = 0,

    @Column(name = "sensor_name")
    val sensorName: String = "",

    @Column(name = "sensor_value")
    val sensorValue: Double = 0.0,

    @Column(name = "received_at")
    val receivedAt: LocalDateTime = LocalDateTime.now()
)