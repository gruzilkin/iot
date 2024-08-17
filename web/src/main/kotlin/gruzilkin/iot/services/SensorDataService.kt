package gruzilkin.iot.services

import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.repositories.SensorDataRepository.SensorDataProjection
import java.security.Principal
import java.time.LocalDateTime

interface SensorDataService {
    fun saveSensorData(sensorDataEvent: SensorDataEvent)

    data class DeviceSensorPair(val deviceId: Long, val sensorName: String )
    fun getDistinctDeviceSensorPairs(): List<DeviceSensorPair>

    fun readData(user: Principal, deviceId: Long, sensorName: String, start: LocalDateTime, end: LocalDateTime, limit: Int): List<SensorDataProjection>
}