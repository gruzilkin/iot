package gruzilkin.iot.services

import gruzilkin.iot.queue.SensorDataEvent

interface SensorDataService {
    fun saveSensorData(sensorDataEvent: SensorDataEvent)

    data class DeviceSensorPair( val deviceId: Long, val sensorName: String )
    fun getDistinctDeviceSensorPairs(): List<DeviceSensorPair>
}