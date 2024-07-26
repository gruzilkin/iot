package gruzilkin.iot.repositories

import gruzilkin.iot.entities.Device
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : JpaRepository<Device, Long> {
    fun findAllByUserIdOrderById(userId: Long): List<Device>
}