package gruzilkin.iot.controllers

import gruzilkin.iot.services.DeviceService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

@Controller
@RequestMapping("/devices")
class DevicesController(
    val deviceService: DeviceService
) {
    @GetMapping
    fun index(principal: Principal, model: Model): String {
        model.addAttribute("name", principal.name)
        model.addAttribute("devices", deviceService.findAll(principal))
        return "devices/index"
    }

    @GetMapping("/{id}")
    fun getDevice(user: Principal, @PathVariable("id") id: Long, model: Model) : String {
        val device = deviceService.findById(user, id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found")
        model.addAttribute("device", device)
        return "devices/edit :: device-edit"
    }

    @PostMapping
    fun addDevice(user: Principal, name: String, model: Model) : String {
        deviceService.save(user, name = name)
        model.addAttribute("devices", deviceService.findAll(user))
        return "devices/index :: device-list"
    }

    @DeleteMapping("/{id}")
    fun deleteDevice(user: Principal, @PathVariable("id") id: Long, model: Model) : String {
        deviceService.deleteById(user, id)
        model.addAttribute("devices", deviceService.findAll(user))
        return "devices/index :: device-list"
    }

    @PostMapping("/{id}/tokens")
    fun addToken(user: Principal, @PathVariable("id") deviceId: Long, model: Model) : String {
        deviceService.generateAndSaveToken(user, deviceId)
        return "redirect:/devices/$deviceId"
    }

    @DeleteMapping("/{deviceId}/tokens/{tokenId}")
    fun deleteToken(user: Principal, @PathVariable("deviceId") deviceId: Long, @PathVariable("tokenId") tokenId: String, model: Model) : String {
        deviceService.deleteToken(user, deviceId, tokenId)
        return getDevice(user, deviceId, model)
    }
}