package gruzilkin.iot.services

import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.repositories.CustomSensorDataRepository
import java.security.Principal
import java.time.Instant

interface SensorDataService {
    fun saveSensorData(sensorDataEvent: SensorDataEvent)

    data class DeviceSensorPair(val deviceId: Long, val sensorName: String )
    fun getDistinctDeviceSensorPairs(): List<DeviceSensorPair>

    fun readData(user: Principal, deviceId: Long, sensorNames: List<String>, start: Instant, end: Instant, limit: Int): Map<String, List<CustomSensorDataRepository.SensorReading>>
}