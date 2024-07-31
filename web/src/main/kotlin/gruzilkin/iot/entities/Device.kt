package gruzilkin.iot.entities

import jakarta.persistence.*

@Entity
@Table(name = "devices")
data class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long = 0,

    @Column
    val name: String = "",

    @OneToMany(mappedBy = "deviceId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val tokens: MutableList<Token> = mutableListOf()
)