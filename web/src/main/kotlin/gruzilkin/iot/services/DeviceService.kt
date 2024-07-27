package gruzilkin.iot.services

import gruzilkin.iot.entities.Device
import java.security.Principal

interface DeviceService {
    fun findAll(user: Principal): List<Device>
    fun findById(user: Principal, id: Long): Device?
    fun save(user: Principal, name: String): Device
    fun deleteById(user: Principal, id: Long)
}