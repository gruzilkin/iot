package gruzilkin.iot.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "sensor_data")
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
    val sensorValue: BigDecimal = BigDecimal.ZERO,

    @Column(name = "received_at")
    val receivedAt: Instant = Instant.now()
)