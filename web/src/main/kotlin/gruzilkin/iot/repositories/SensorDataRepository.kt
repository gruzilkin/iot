package gruzilkin.iot.repositories

import gruzilkin.iot.entities.SensorData
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    @Query("""(SELECT sensor_value as sensorValue, received_at as receivedAt
				FROM sensor_data
				WHERE device_id = :deviceId AND sensor_name = :sensorName
                AND received_at >= :start AND received_at <= :end 
				ORDER BY id ASC LIMIT 1)
				UNION
				(SELECT sensor_value as sensorValue, received_at as receivedAt
				FROM sensor_data
				JOIN sensor_data_weights USING (id)
				WHERE device_id = :deviceId AND sensor_name = :sensorName
                AND received_at >= :start AND received_at <= :end
				ORDER BY weight DESC LIMIT :limit)
				UNION
				(SELECT sensor_value as sensorValue, received_at as receivedAt
				FROM sensor_data
				WHERE device_id = :deviceId AND sensor_name = :sensorName
                AND received_at >= :start AND received_at <= :end
				ORDER BY id DESC LIMIT 1)""", nativeQuery = true)
    fun smartFindByDeviceIdAndSensorName(@Param("deviceId") deviceId: Long, @Param("sensorName") sensorName: String, @Param("start") start: LocalDateTime, @Param("end") end: LocalDateTime, @Param("limit") limit: Int): List<SensorDataProjection>
}