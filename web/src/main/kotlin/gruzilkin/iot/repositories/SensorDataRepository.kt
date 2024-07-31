package gruzilkin.iot.repositories

import gruzilkin.iot.entities.SensorData
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SensorDataRepository : JpaRepository<SensorData, Int> {
    @Query("SELECT DISTINCT s.deviceId, s.sensorName FROM SensorData s")
    fun findDistinctDeviceIdAndSensorName(): List<Array<Any>>

    interface SensorDataProjection {
        val sensorValue: Double
        val receivedAt: LocalDateTime
    }
    fun findByDeviceIdAndSensorName(deviceId: Long, sensorName: String, sort: Sort): List<SensorDataProjection>
}