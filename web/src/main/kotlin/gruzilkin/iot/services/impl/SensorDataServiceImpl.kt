package gruzilkin.iot.services.impl

import gruzilkin.iot.entities.SensorData
import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.services.SensorDataService
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class SensorDataServiceImpl(
    val sensorDataRepository: SensorDataRepository
) : SensorDataService {
    override fun saveSensorData(sensorDataEvent: SensorDataEvent) {
        sensorDataRepository.save(
            SensorData(
                deviceId = sensorDataEvent.deviceId,
                sensorName = sensorDataEvent.sensorName,
                sensorValue = sensorDataEvent.sensorValue,
                receivedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(sensorDataEvent.receivedAt), ZoneOffset.UTC)
            )
        )
    }

    override fun getDistinctDeviceSensorPairs(): List<SensorDataService.DeviceSensorPair> {
        return sensorDataRepository.findDistinctDeviceIdAndSensorName()
            .map {
                SensorDataService.DeviceSensorPair(it[0] as Long, it[1] as String)
            }.toList()
    }
}