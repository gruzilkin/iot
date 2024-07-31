package gruzilkin.iot.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "access_tokens")
data class Token (
    @Id
    @Column(name = "token")
    val token: String,

    @Column(name = "device_id")
    val deviceId: Long,

    @Column(name = "created_at")
    var createdAt: LocalDateTime,

    @Column(name = "valid_until")
    var validUntil: LocalDateTime
) {
    constructor(deviceId: Long) : this(UUID.randomUUID().toString().replace("-", ""),
        deviceId,
        LocalDateTime.now(),
        LocalDateTime.now().plusYears(1))
    constructor() : this(0)
}