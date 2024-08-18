package gruzilkin.iot.repositories

import gruzilkin.iot.entities.SensorData
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

@Repository
interface SensorDataRepository : JpaRepository<SensorData, Int>, CustomSensorDataRepository {
    @Query("SELECT DISTINCT s.deviceId, s.sensorName FROM SensorData s")
    fun findDistinctDeviceIdAndSensorName(): List<Array<Any>>
}