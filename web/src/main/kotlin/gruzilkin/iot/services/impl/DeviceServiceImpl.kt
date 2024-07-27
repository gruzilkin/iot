package gruzilkin.iot.services.impl

import gruzilkin.iot.entities.Device
import gruzilkin.iot.repositories.DeviceRepository
import gruzilkin.iot.services.DeviceService
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class DeviceServiceImpl (val entityManager: EntityManager, val devicesRepository: DeviceRepository) : DeviceService {
    override fun findAll(user: Principal): List<Device> {
        val devices = devicesRepository.findAllByUserIdOrderById(user.name.toLong())
        devices.forEach(entityManager::detach)
        return devices
    }

    override fun findById(user: Principal, id: Long): Device? {
        val device = devicesRepository.findByIdAndUserId(id, user.name.toLong())
        device?.let(entityManager::detach)
        return device
    }

    override fun save(user: Principal, name: String): Device {
        return devicesRepository.save(Device(userId = user.name.toLong(), name = name))
    }

    override fun deleteById(user: Principal, id: Long) {
        if (devicesRepository.existsByIdAndUserId(id, user.name.toLong())) {
            devicesRepository.deleteById(id)
        }
    }
}