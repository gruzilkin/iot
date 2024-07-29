package gruzilkin.iot.services

import gruzilkin.iot.queue.SensorDataEvent

interface SensorDataService {
    fun saveSensorData(sensorDataEvent: SensorDataEvent)
}