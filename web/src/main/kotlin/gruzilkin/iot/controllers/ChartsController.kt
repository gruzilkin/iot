package gruzilkin.iot.controllers

import gruzilkin.iot.services.SensorDataService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.RoundingMode
import java.security.Principal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Controller
@RequestMapping("/charts")
class ChartsController(
    val sensorDataService: SensorDataService
) {
    @GetMapping("/{deviceId}")
    fun index(user: Principal, @PathVariable("deviceId") deviceId: Long,  model: Model): String {
        val start = Instant.ofEpochMilli(0)
        val end = Instant.now()
        val sensorNames= listOf("temperature", "humidity", "voc", "ppm")

        val data = sensorDataService.readData(user, deviceId, sensorNames, start, end, 1000)
        for ((sensorName, list) in data) {
            val forJson = list.map {
                listOf(
                    it.receivedAt.toEpochMilli(),
                    it.value.setScale(3, RoundingMode.FLOOR).toDouble()
                )
            }
            model.addAttribute(sensorName, forJson)
        }

        return "charts/index"
    }

    @GetMapping("/{deviceId}/partial")
    fun partial(
        user: Principal,
        @PathVariable("deviceId") deviceId: Long,
        @RequestParam("start", required = false) start: Long?,
        @RequestParam("end", required = false) end: Long?
    ): ResponseEntity<Any> {
        val startTime = start?.let { Instant.ofEpochMilli(it) } ?: Instant.ofEpochMilli(0)
        val endTime = end?.let { Instant.ofEpochMilli(it) } ?: Instant.now()
        val sensorNames = listOf("temperature", "humidity", "voc", "ppm")

        val response = mutableMapOf<String, List<List<*>>>()
        val data = sensorDataService.readData(user, deviceId, sensorNames, startTime, endTime, 1000)
        for ((sensorName, list) in data) {
            response[sensorName] = list.map {
                listOf(
                    it.receivedAt.toEpochMilli(),
                    it.value.setScale(3, RoundingMode.FLOOR).toDouble()
                )
            }
        }

        return ResponseEntity.ok(response)
    }
}