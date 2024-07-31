package gruzilkin.iot.repositories

import gruzilkin.iot.entities.SensorData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SensorDataRepository : JpaRepository<SensorData, Int> {
    @Query("SELECT DISTINCT s.deviceId, s.sensorName FROM SensorData s")
    fun findDistinctDeviceIdAndSensorName(): List<Array<Any>>
}