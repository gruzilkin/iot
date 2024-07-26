package gruzilkin.iot.repositories

import gruzilkin.iot.entities.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<Token, String> {
    fun findByDeviceId(deviceId: Long): List<Token>
}