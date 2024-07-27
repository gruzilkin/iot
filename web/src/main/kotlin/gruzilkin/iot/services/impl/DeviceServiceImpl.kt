package gruzilkin.iot.services.impl

import gruzilkin.iot.entities.Device
import gruzilkin.iot.entities.Token
import gruzilkin.iot.repositories.DeviceRepository
import gruzilkin.iot.repositories.TokenRepository
import gruzilkin.iot.services.DeviceService
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal

@Service
@Transactional
class DeviceServiceImpl (val entityManager: EntityManager, val devicesRepository: DeviceRepository, val tokenRepository: TokenRepository) : DeviceService {
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
        } else {
            throw IllegalArgumentException("Device not found")
        }
    }

    override fun generateAndSaveToken(user: Principal, id: Long) : String{
        val device = devicesRepository.findByIdAndUserId(id, user.name.toLong())
        if (device == null) {
            throw IllegalArgumentException("Device not found")
        }

        val token = Token(deviceId = device.id!!)
        device.tokens.add(token)

        return token.token
    }

    override fun deleteToken(user: Principal, id: Long, token: String) {
        val device = devicesRepository.findByIdAndUserId(id, user.name.toLong()) ?: throw IllegalArgumentException("Device not found")
        val t =  device.tokens.firstOrNull { it.token == token } ?: throw IllegalArgumentException("Token not found")
        device.tokens.remove(t)
        tokenRepository.deleteById(t.token)
    }
}