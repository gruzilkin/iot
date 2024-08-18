package gruzilkin.iot.services.impl

import gruzilkin.iot.entities.SensorData
import gruzilkin.iot.queue.SensorDataEvent
import gruzilkin.iot.repositories.CustomSensorDataRepository
import gruzilkin.iot.repositories.SensorDataRepository
import gruzilkin.iot.services.DeviceService
import gruzilkin.iot.services.SensorDataService
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
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
                receivedAt = Instant.ofEpochMilli(sensorDataEvent.receivedAt)
            )
        )
    }

    override fun getDistinctDeviceSensorPairs(): List<SensorDataService.DeviceSensorPair> {
        return sensorDataRepository.findDistinctDeviceIdAndSensorName()
            .map {
                SensorDataService.DeviceSensorPair(it[0] as Long, it[1] as String)
            }.toList()
    }

    override fun readData(user: Principal, deviceId: Long, sensorNames: List<String>, start: Instant, end: Instant, limit: Int): Map<String, List<CustomSensorDataRepository.Point>> {
        if (!deviceService.canAccess(user, deviceId)) {
            throw IllegalArgumentException("Access denied")
        }

        return runBlocking {
            withContext(Dispatchers.IO) {
                sensorNames.map { sensorName ->
                    async {
                        val data = sensorDataRepository.smartFindByDeviceIdAndSensorName(
                            deviceId,
                            sensorName,
                            start,
                            end,
                            limit
                        ).sortedBy { it.receivedAt }
                        sensorName to data
                    }
                }.awaitAll().toMap()
            }
        }
    }
}