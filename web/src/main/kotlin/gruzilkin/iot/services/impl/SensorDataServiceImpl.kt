package gruzilkin.iot.services.impl

import gruzilkin.iot.entities.SensorData
import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.repositories.SensorDataRepository.SensorDataProjection
import gruzilkin.iot.services.DeviceService
import gruzilkin.iot.services.SensorDataService
import org.springframework.stereotype.Service
import java.security.Principal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class SensorDataServiceImpl(
    val sensorDataRepository: SensorDataRepository,
    val deviceService: DeviceService
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

    override fun readData(user: Principal, deviceId: Long, sensorName: String): List<SensorDataProjection> {
        if (!deviceService.canAccess(user, deviceId)) {
            throw IllegalArgumentException("Access denied")
        }
        return sensorDataRepository.findByDeviceIdAndSensorName(deviceId, sensorName)
    }
}