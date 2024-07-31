package gruzilkin.iot.services

import gruzilkin.iot.entities.Device
import java.security.Principal

interface DeviceService {
    fun canAccess(user: Principal, id: Long): Boolean

    fun findAll(user: Principal): List<Device>
    fun findById(user: Principal, id: Long): Device?
    fun save(user: Principal, name: String): Device
    fun deleteById(user: Principal, id: Long)

    fun generateAndSaveToken(user: Principal, id: Long) : String
    fun deleteToken(user: Principal, id: Long, token: String)
}