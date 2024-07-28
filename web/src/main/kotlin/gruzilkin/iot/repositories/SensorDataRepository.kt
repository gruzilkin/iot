package gruzilkin.iot.repositories

import gruzilkin.iot.entities.SensorData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SensorDataRepository:JpaRepository<SensorData, Int> { }