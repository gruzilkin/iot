package gruzilkin.iot.services

import gruzilkin.iot.queue.SensorDataEvent
import java.security.Principal

interface SensorDataService {
    fun saveSensorData(sensorDataEvent: SensorDataEvent)

    data class DeviceSensorPair(val deviceId: Long, val sensorName: String )
    fun getDistinctDeviceSensorPairs(): List<DeviceSensorPair>

    fun readData(user: Principal, deviceId: Long, sensorName: String): List<Array<Any>>
}