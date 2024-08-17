package gruzilkin.iot.controllers

import gruzilkin.iot.services.SensorDataService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
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
        for (sensorName in listOf("temperature", "humidity", "voc", "ppm")) {
            val start = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)
            val end = LocalDateTime.now()
            val data = sensorDataService.readData(user, deviceId, sensorName, start, end, 1000)
            val forJson = data.map {
                arrayOf(
                    it.receivedAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
                    BigDecimal.valueOf(it.sensorValue).setScale(3, RoundingMode.FLOOR).toDouble()
                )
            }.toList()
            model.addAttribute(sensorName, forJson)
        }
        return "charts/index"
    }

    @GetMapping("/{deviceId}/partial")
    fun partial(user: Principal, @PathVariable("deviceId") deviceId: Long, @RequestParam("start") start: Long, @RequestParam("end") end: Long): ResponseEntity<Any> {
        val response = mutableMapOf<String, Any>()
        for (sensorName in listOf("temperature", "humidity", "voc", "ppm")) {
            val startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneOffset.UTC)
            val endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneOffset.UTC)
            val data = sensorDataService.readData(user, deviceId, sensorName, startTime, endTime, 100)
            val forJson = data.map {
                arrayOf(
                    it.receivedAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
                    BigDecimal.valueOf(it.sensorValue).setScale(3, RoundingMode.FLOOR).toDouble()
                )
            }.toList()
            response[sensorName] = forJson
        }
        return ResponseEntity.ok(response)
    }
}