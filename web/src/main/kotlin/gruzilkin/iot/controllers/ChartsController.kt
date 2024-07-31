package gruzilkin.iot.controllers

import gruzilkin.iot.services.SensorDataService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal
import java.time.ZoneOffset

@Controller
@RequestMapping("/charts")
class ChartsController(
    val sensorDataService: SensorDataService
) {
    @GetMapping("/{deviceId}")
    fun index(user: Principal, @PathVariable("deviceId") deviceId: Long,  model: Model): String {
        for (sensorName in listOf("temperature", "humidity", "voc", "ppm")) {
            val data = sensorDataService.readData(user, deviceId, sensorName)
            val forJson = data.map {
                arrayOf(it.sensorValue, it.receivedAt.toInstant(ZoneOffset.UTC).toEpochMilli())
            }.toList()
            model.addAttribute(sensorName, forJson)
        }
        return "charts/index"
    }

}