package gruzilkin.iot.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "sensor_data_weights")
data class SensorDataWeight(
    @Id
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "weight")
    val deviceId: Double = 0.0,
)