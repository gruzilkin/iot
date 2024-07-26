package gruzilkin.iot.controllers

import gruzilkin.iot.entities.Device
import gruzilkin.iot.repositories.DeviceRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Controller
@RequestMapping("/devices")
class DevicesController(val devicesRepository: DeviceRepository) {
    @GetMapping
    fun index(principal: Principal, model: Model): String {
        model.addAttribute("name", principal.name)
        model.addAttribute("devices", devicesRepository.findAllByUserIdOrderById(principal.name.toLong()))
        return "devices/index"
    }

    @PostMapping
    fun addDevice(user: Principal, name: String, model: Model) : String {
        val device = devicesRepository.save(Device(userId = user.name.toLong(), name = name))
        model.addAttribute("devices", devicesRepository.findAllByUserIdOrderById(user.name.toLong()))
        return "devices/index :: device-list"
    }

    @DeleteMapping("/{id}")
    fun deleteDevice(user: Principal, @PathVariable("id") id: Long, model: Model) : String {
        devicesRepository.deleteById(id)
        model.addAttribute("devices", devicesRepository.findAllByUserIdOrderById(user.name.toLong()))
        return "devices/index :: device-list"
    }
}