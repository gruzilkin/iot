package gruzilkin.iot.repositories.impl

import gruzilkin.iot.repositories.CustomSensorDataRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CustomSensorDataRepositoryImpl : CustomSensorDataRepository {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun smartFindByDeviceIdAndSensorName(
        deviceId: Long,
        sensorName: String,
        start: Instant,
        end: Instant,
        limit: Int
    ): List<CustomSensorDataRepository.Point> {
        val query = entityManager.createNativeQuery("""(SELECT sensor_value, received_at
				FROM sensor_data
				WHERE device_id = :deviceId AND sensor_name = :sensorName
                AND received_at >= :start AND received_at <= :end 
				ORDER BY id ASC LIMIT 1)
				UNION
				(SELECT sensor_value, received_at
				FROM sensor_data
				JOIN sensor_data_weights USING (id)
				WHERE device_id = :deviceId AND sensor_name = :sensorName
                AND received_at >= :start AND received_at <= :end
				ORDER BY weight DESC LIMIT :limit)
				UNION
				(SELECT sensor_value, received_at
				FROM sensor_data
				WHERE device_id = :deviceId AND sensor_name = :sensorName
                AND received_at >= :start AND received_at <= :end
				ORDER BY id DESC LIMIT 1)""")
        query.setParameter("deviceId", deviceId)
        query.setParameter("sensorName", sensorName)
        query.setParameter("start", start)
        query.setParameter("end", end)
        query.setParameter("limit", limit)
        val resultList = query.resultList
        return resultList.map {
            val row = it as Array<*>
            val receivedAt = when(val time = row[1]) {
                is LocalDateTime -> time.toInstant(ZoneOffset.UTC)
                is OffsetDateTime -> time.toInstant()
                is Instant -> time
                else -> {
                    throw IllegalStateException("Unexpected type ${time?.javaClass}")
                }
            }
            CustomSensorDataRepository.Point(receivedAt, row[0] as BigDecimal)
        }
    }
}